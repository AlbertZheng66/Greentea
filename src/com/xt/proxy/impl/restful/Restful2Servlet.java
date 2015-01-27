package com.xt.proxy.impl.restful;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.log.LogWriter;
import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.proxy.event.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 用于处理 Rest 风格的请求调用，结果是以“JSON”格式进行输出的。
 * 输入的格式是：/demo/r/com.xt.comm.service.DownloadService?m=download&p=['README.TXT']
 *
 * @author albert
 */
public class Restful2Servlet extends RestfulServlet {
    private static final long serialVersionUID = -1071711961355527033L;
    
    protected final Logger logger = Logger.getLogger(Restful2Servlet.class);
        
    /**
     * 使用缩写映射地址与对应的类
     */
    protected Map<String, String> classMapping;
    
     /**
     * 使用缩写映射地址与对应的类
     */
    protected Map<String, String> methodMapping;

    public Restful2Servlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init(); 
        classMapping = SystemConfiguration.getInstance().readMap("restful.classMapping");
        methodMapping = SystemConfiguration.getInstance().readMap("restful.methodMapping");
    }
    
    

    @Override
    protected Request createRequest(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        String[] segs = pathInfo.split("[/]");
        String className = null;
        for (int i = segs.length - 1; i >= 0; i--) {
            // 最后一个为类名
            String seg = segs[i];
            if (StringUtils.isNotEmpty(seg)) {
                className = seg;
                break;
            }
        }
        if (StringUtils.isEmpty(className)) {
            throw new RestfulException(String.format("不能从上下文[%s]解析出类名。", pathInfo));
        }
        // 先使用映射进行查找
        LogWriter.debug2(logger, "类名:[%s], mapping:[%s]", className, classMapping);
        if(classMapping.containsKey(className)) {
            className = String.valueOf(classMapping.get(className));
        } 
        // 测试是否定义
        Class clazz = ClassHelper.getClass(className);
        if (clazz.isAssignableFrom(IService.class)) {
            throw new RestfulException(String.format("类[%s]必须继承接口[%s]。", className, clazz));
        }
        Request request = new Request();
        request.setService(clazz);
        String methodName = req.getParameter("m");
        if (StringUtils.isEmpty(methodName)) {
            throw new RestfulException(String.format("方法名[参数“m”]不能为空。", methodName));
        }
        if (methodMapping.containsKey(methodName)) {
            methodName = methodMapping.get(methodName);
        }
        
        request.setMethodName(methodName);
        String paramsStr = req.getParameter("p");
        Object[] _params = parseParams(paramsStr);
        

        
        Class<?>[] pTypes = parseParameterTypes(request.getService(), request.getMethodName(), _params);
        request.setParamTypes(pTypes);
        if (_params.length != pTypes.length) {
            throw new RestfulException(String.format("方法参数的个数[%d]和定义的个数[%s]不相等。", _params.length, pTypes.length));
        }
        // 转换参数类型
        List params = new ArrayList();
        for (int i = 0; i < pTypes.length; i++) {
            Class<?> targetClass = pTypes[i];
            Object value = _params[i];
            // 当类型不同时自动进行类型转换
            if (isConverted(value, targetClass)) {
                Class sourceClass = value.getClass();
                Converter converter = ConverterFactory.getInstance().getConverter(sourceClass, targetClass);
                value = converter.convert(sourceClass, targetClass, value);
            }
            params.add(value);
        }
        
        // 如果有文件类型
        
        request.setParams(params.toArray());
        return request;
    }

    private static boolean isConverted(Object value, Class targetClass) {
        return value != null && targetClass != null
                && targetClass != Object.class && targetClass != value.getClass() /*
                 * &&!targetClass.isAssignableFrom(value.getClass()) ? 为啥去掉了? 加上会有什么问题？
                 */;
    }

}
