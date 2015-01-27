package com.xt.proxy.impl.restful;

import com.xt.core.json.JsonBuilder;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.proxy.event.Request;
import com.xt.proxy.impl.http.json.JsonClientServlet;
import java.lang.reflect.Method;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * 用于处理 Rest 风格的请求调用，结果是以“JSON”格式进行输出的。
 * 输入的格式是：packge1/packge2/.../className/methodName?params=[json style]
 * @deprecated 
 * @author albert
 */
public class RestfulServlet extends JsonClientServlet {
    
    /**
     * 基础路径
     */
    private String basePath = "";
    private static final long serialVersionUID = 2720862868982328501L;
    private final Logger logger = Logger.getLogger(RestfulServlet.class);

    public RestfulServlet() {
    }

    @Override
    public void init() throws ServletException {
        super.init();
        basePath = SystemConfiguration.getInstance().readString("system.restful.bastPath", "");
    }

    @Override
    protected Request createRequest(HttpServletRequest req) {
        Request request = new Request();
        String path = req.getServletPath();
        parseClassAndMethod(request, path);
        String _params = req.getParameter("params");
        Object[] params = parseParams(_params);
        request.setParams(params);
        Class<?>[] pTypes = parseParameterTypes(request.getService(), request.getMethodName(), params);
        request.setParamTypes(pTypes);
        return request;
    }

    /**
     * 根据路径推断出其对应的类。
     * 路径的构建规则是：packge1/packge2/.../className/methodName
     * @param path
     * @return
     */
    protected void parseClassAndMethod(Request request, String path) {
        StringBuilder className = new StringBuilder(basePath);
        String[] segs = path.split("[/]");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String methodName = segs[segs.length - 1].trim();  // 最后一端是方法名称
        for (int i = 0; i < segs.length - 1; i++) {
            String name = segs[i];
            if (className.length() > 0) {
                className.append(".");
            }
            className.append(name);
        }
        Class serviceClass = ClassHelper.getClass(className.toString());
        request.setService(serviceClass);
        request.setMethodName(methodName);
    }

    protected Object[] parseParams(String paramsStr) {
        Object[] params = null;
        if (StringUtils.isEmpty(paramsStr)) {
            params = new Object[0];
        } else {
            JsonBuilder builder = new JsonBuilder();
            JSON json = JSONArray.fromObject(paramsStr);
            Object obj = builder.parse(json);
            if (obj == null) {
                params = new Object[0];
            } else if (obj.getClass().isArray()) {
                params = (Object[]) obj;
            } else if (obj instanceof List) {
                params = ((List) obj).toArray();
            } else {
                throw new RestfulException(String.format("参数[%s]的类型不能被处理。", obj));
            }
        }
        return params;
    }

    protected Class<?>[] parseParameterTypes(Class service, String methodName, Object[] params) {
        Method[] methods = service.getMethods();
        Method method = null;
        for (int i = 0; i < methods.length; i++) {
            method = methods[i];
            if (method.getName().equals(methodName)
                    && (method.getParameterTypes().length == params.length)) {
                break;
            } else {
                method = null;
            }
        }
        if (method == null) {
            throw new RestfulException(String.format("类[%s]的方法[%s]参数个数[%d]不存在。",
                    service, methodName, params.length));
        }
        return method.getParameterTypes();
    }
}
