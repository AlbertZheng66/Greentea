/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.proxy.impl.http.json;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.json.JsonBuilder;
import com.xt.core.log.LogWriter;
import com.xt.core.service.IService;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.IOHelper;
import com.xt.gt.jt.proc.result.DownloadedFileInfo;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.jt.http.GreenTeaGeneralServlet;
import com.xt.gt.sys.SystemConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * 当浏览器主要采用 JSON 的方式进行交互时，采用此Servlet进行处理。
 *
 * @author albert
 */
public class JsonClientServlet extends GreenTeaGeneralServlet {

    public static final String CONTENT_TYPE_DEFAULT_ENCODING = "UTF-8";

    private static final long serialVersionUID = -4149554276421416288L;

    /**
     * 参数名称
     */
    public static final String PARAM_JSON_VALUE = "jsonValue";
    /**
     * 默认的流类型的参数值
     */
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream";

    /**
     * 使用 JSON 时使用的编码格式，可通过参数“json.content.encoding”进行配置，默认为：utf-8。
     */
    private final String contentEncoding = SystemConfiguration.getInstance().readString("json.content.encoding", "UTF-8");

    protected final Logger logger = Logger.getLogger(JsonClientServlet.class);

    @Override
    protected Request createRequest(HttpServletRequest req) {
        String jsonValue = null;
        Map<String, UploadedFile> uploadedFiles = new HashMap(3);
        if (isMultipart(req)) {
            jsonValue = parseMultipart(req, jsonValue, uploadedFiles);
        } else {
            jsonValue = req.getParameter(PARAM_JSON_VALUE);
        }
        if (jsonValue == null) {
            throw new JsonClientException(String.format("缺少名称为[%s]的参数", PARAM_JSON_VALUE));
        }
        LogWriter.debug(logger, "input json", jsonValue);
        JSONObject jsonObj = JSONObject.fromObject(jsonValue);

        Request request = new Request();
        String className = jsonObj.getString("serviceClassName");
        if (StringUtils.isEmpty(className)) {
            throw new JsonClientException("类名称不能为空。");
        }

        Class clazz = ClassHelper.getClass(className);
        request.setService(clazz);
        if (clazz.isAssignableFrom(IService.class)) {
            throw new JsonClientException(String.format("类[%s]必须实现接口[%s]。",
                    clazz.getName(), IService.class.getName()));
        }
        // 模拟客户端的本地请求(注入调用方法)
        Object invokedMethodObject = jsonObj.get("invokedMethods");
        if (invokedMethodObject != null) {
            JSONArray invokedMethods = jsonObj.getJSONArray("invokedMethods");
            IService serviceObject = (IService) ClassHelper.newInstance(clazz);
            for (int i = 0; i < invokedMethods.size(); i++) {
                Object obj = invokedMethods.get(i);
                if (obj instanceof JSONObject) {
                    String localMethodName = ((JSONObject) obj).getString("localMethodName");
                    try {
                        if (StringUtils.isEmpty(localMethodName)) {
                            throw new JsonClientException("本地方法名称不能为空");
                        }
                        Method method = getMethod(clazz, localMethodName);
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        JSONArray params = ((JSONObject) obj).getJSONArray("localParams");
                        List realParams = parseParams(params, parameterTypes, method, uploadedFiles);
                        method.invoke(serviceObject, realParams.toArray());
                    } catch (Exception ex) {
                        throw new JsonClientException(String.format("调用类[%s]的方法[%s]时出现异常。", className, localMethodName), ex);
                    }
                }
            }
            request.setServiceObject(serviceObject);
        }
        request.setMethodName(jsonObj.getString("methodName"));

        // 从方法名称构建参数类型
        Method method = getMethod(clazz, request.getMethodName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        request.setParamTypes(parameterTypes);

        JSONArray params = jsonObj.getJSONArray("params");
        List realParams = parseParams(params, parameterTypes, method, uploadedFiles);
        request.setParams(realParams.toArray());
        return request;
    }

    protected String parseMultipart(HttpServletRequest req, String jsonValue, Map<String, UploadedFile> uploadedFiles) {
        LogWriter.debug(logger, "处理上传文件的情况...");
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);// 设置缓冲,这个值决定了是fileinputstream还是 bytearrayinputstream
        // factory.setRepository(new File("d:\\temp"));//设置临时存放目录,默认是new File(System.getProperty("java.io.tmpdir"))
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(100 * 1024 * 1024);//100M
        try {
            // Parse the request
            List items = upload.parseRequest(req); /* FileItem */

            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                LogWriter.debug(logger, "sname", item.getFieldName());
                if (item.isFormField()) {

                    if (PARAM_JSON_VALUE.equals(item.getFieldName())) {
                        jsonValue = item.getString(contentEncoding);
                    }
                } else {
                    UploadedFile uf = new UploadedFile(item.getFieldName(), item.getName(),
                            item.getContentType(), item.getSize());
                    LogWriter.debug(logger, "getSize", item.getSize());
                    try {
                        uf.setInputStream(item.getInputStream());
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(JsonClientServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    uploadedFiles.put(uf.getFieldName(), uf);
                }
            }

        } catch (FileUploadException ex) {
            java.util.logging.Logger.getLogger(JsonClientServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.io.UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(JsonClientServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonValue;
    }

    protected boolean isMultipart(HttpServletRequest req) {
        return ServletFileUpload.isMultipartContent(req);
    }

    private Method getMethod(Class serviceClass, String methodName) {
        Method method = ClassHelper.getMethod(serviceClass, methodName);
        if (method == null) {
            throw new JsonClientException(String.format("类[%s]的方法[%s]不存在。",
                    serviceClass, methodName));
        }
        return method;
    }

    @Override
    protected void outputResponse(HttpServletRequest req, HttpServletResponse res, Response response) throws IOException {
//        LogWriter.debug(logger, "thread id=", Thread.currentThread().getId());
//        LogWriter.debug(logger, "res id=", res);
        // 精简处理时不需要输
        final boolean concise = BooleanUtils.isTrue(req.getParameter("c"));
        LogWriter.debug(logger, "concise=", concise);
        OutputStream os = res.getOutputStream();
        // 首先判断返回值的类型
        Object returnVal = response.getReturnValue();
        if (returnVal == null) {
            if (concise) {
                outputSimpleObject(res, os, returnVal);
            } else {
                outputObject(res, os, response);
            }
        } else if (returnVal instanceof InputStream) {
            // 以文件的形式输出
            outputInputStream(res, os, (InputStream) returnVal);
        } else if (returnVal instanceof DownloadedFileInfo) {
            // 以文件的形式输出
            outputDownloadFile(res, os, (DownloadedFileInfo) returnVal);
        } else if (returnVal instanceof Serializable) {
            if (concise) {
                if (!outputSimpleObject(res, os, returnVal)) {
                    if (returnVal instanceof Serializable) {
                        outputObject(res, os, (Serializable) returnVal);
                    } else {
                        throw new JsonClientException(String.format("不能此返回值的类型[%s]，需要输入流、字节数组或者可序列化类型。", returnVal.getClass()));
                    }
                }
            } else {
                outputObject(res, os, response);
            }
        } else {
            throw new JsonClientException(String.format("不能此返回值的类型[%s]，需要输入流、字节数组或者可序列化类型。", returnVal.getClass()));
        }
        os.flush();
    }

    protected void outputBytes(HttpServletResponse res, OutputStream os, byte[] bytes) throws IOException {
        res.setHeader("contentType", CONTENT_TYPE_STREAM);
        os.write(bytes);
    }

    protected void outputInputStream(HttpServletResponse res, OutputStream os, InputStream is) throws IOException {
        LogWriter.debug(logger, "outputInputStream.......");
        res.setContentType(CONTENT_TYPE_STREAM);
        res.setHeader("Content-disposition", "attachment; filename=unknowed");
        IOHelper.i2o(is, os, true, false);
    }

    protected void outputDownloadFile(HttpServletResponse res, OutputStream os,
            DownloadedFileInfo fd) throws IOException {
        LogWriter.info(logger, "contextType=", fd.getContentType());
        if (fd.getContentType() == null) {
            res.setContentType(CONTENT_TYPE_STREAM);
        } else {
            res.setContentType(fd.getContentType());
        }
        if (fd.getFileName() == null) {
            res.setHeader("Content-disposition", "attachment; filename=unknowed");
        } else {
            res.setHeader("Content-disposition", "attachment; filename=" + fd.getFileName());
        }

        if (fd.getContentLength() > 0) {
            res.setHeader("Content-Length", String.valueOf(fd.getContentLength()));
        }
        IOHelper.i2o(fd.getInputStream(), os, true, false);
    }

    @Override
    protected void outputThrowable(HttpServletRequest req, HttpServletResponse res, Throwable throwable) {
        // logger.error("输出异常", throwable);

//        StringWriter sw = new StringWriter();
//        throwable.printStackTrace(new PrintWriter(sw));
//        outputObject(res, sw.toString());
        // 直接输出对象
        ExceptionWrapper wrapper = new ExceptionWrapper();
        wrapper.load(throwable);
        try {
            OutputStream os = res.getOutputStream();
            outputObject(res, os, wrapper);
            os.flush();
        } catch (IOException ex) {
            logger.error("流输出错误。", ex);
        }
    }

    protected boolean outputSimpleObject(HttpServletResponse res, OutputStream os, Object value) {
        logger.info("outputSimpleObject value=" + value);
        if (value == null) {
            writeObject0("", res, os);
            return true;
        }

        Class valueClass = value.getClass();
        logger.info("outputSimpleObject valueClass=" + valueClass);
        // 处理原始类型
        if (valueClass.isPrimitive() || value instanceof Number || value instanceof Character || value instanceof String || value instanceof Boolean) {
            logger.info("outputSimpleObject value=" + value);
            writeObject0(String.valueOf(value), res, os);
            return true;
        }
        logger.info("outputSimpleObject (not simple value)valueClass=" + valueClass);
        return false;
    }

    protected void outputObject(HttpServletResponse res, OutputStream os, Serializable obj) {
        if (obj == null) {
            throw new JsonClientException("返回对象不能为空。");
        }
        JsonBuilder jsonBuilder = new JsonBuilder();
        String str = jsonBuilder.build(obj);
        writeObject0(str, res, os);
    }

    private void writeObject0(String str, HttpServletResponse res, OutputStream os) {
        // LogWriter.info(logger, "outputText", str);
        logger.info("outputText=" + str);
        res.setHeader("contentType", "text/json;charset=" + CONTENT_TYPE_DEFAULT_ENCODING);
        try {
            os.write(str.getBytes(CONTENT_TYPE_DEFAULT_ENCODING));
        } catch (IOException ex) {
            logger.error("流输出错误。", ex);
        }
    }

    private List parseParams(JSONArray params, Class<?>[] parameterTypes,
            Method method, Map<String, UploadedFile> uploadedFiles) throws JsonClientException {
        LogWriter.debug(logger, "parseParams..parameterTypes", ToStringBuilder.reflectionToString(parameterTypes));
        LogWriter.debug(logger, "parseParams..params", ToStringBuilder.reflectionToString(params));
        LogWriter.debug(logger, "parseParams..method", method);
        List realParams = new ArrayList();
        if (params != null && params.size() > 0) {
            if (params.size() != parameterTypes.length) {
                throw new JsonClientException(String.format("传入的参数个数[%d]与方法[%s]的参数个数[%d]不符。", params.size(), method.getName(), parameterTypes.length));
            }
            JsonBuilder builder = new JsonBuilder();
            int index = 0;
            for (Iterator iter = params.iterator(); iter.hasNext(); index++) {
                Object _param = iter.next();
                LogWriter.debug(logger, "params", params);
                Object param = null;
                // 当参数类型是输入流（InputStream）时，需要单独处理
                if (InputStream.class.isAssignableFrom(parameterTypes[index])) {
                    JSONObject json = (JSONObject) _param;  // 这个对象对应客户端的 UploadFile 对象。
                    if (json.containsKey("propertyName")) {
                        String propertyName = json.getString("propertyName");
                        UploadedFile uf = uploadedFiles.get(propertyName);
                        if (uf != null) {
                            param = uf.getInputStream();
                        }
                    }
                    realParams.add(param);
                    continue;  // 避免后面的类型转换
                }
                if (_param instanceof JSON) {
                    JSON json = (JSON) _param;
                    LogWriter.debug(logger, "json=", json);
                    param = builder.parse(json);
                } else {
                    param = _param;
                }
                // 在某些简写的情况下，需要在 Map 类型和 Object 类型之间进行映射
                Class parameterType = parameterTypes[index];
                if (canConvert(param, parameterType)) {
                    Converter converter = ConverterFactory.getInstance().getConverter(param.getClass(),
                            parameterType);
                    param = converter.convert(param.getClass(), parameterType, param);
                }
                realParams.add(param);
            }
        } else {
            if (parameterTypes.length > 0) {
                throw new JsonClientException(String.format("传入的参数个数[0]与方法[%s]的参数个数[%d]不符。", method.getName(), parameterTypes.length));
            }
        }
        return realParams;
    }

    /**
     * 参数及其类型是否能转换
     *
     * @param param
     * @param parameterTypes
     * @param index
     * @return
     */
    private boolean canConvert(Object param, Class<?> parameterType) {
        return (param != null && param.getClass() != parameterType);
//        if(param == null) {
//            return false;
//        }

//        if (parameterType == Object.class) {
//            return true;
//        }
//        return (!parameterType.isAssignableFrom(param.getClass()));
    }

}
