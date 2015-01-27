package com.xt.proxy.utils;

import com.xt.core.cm.impl.IPOModifier;
import com.xt.core.log.LogWriter;
import com.xt.proxy.Proxy;
import com.xt.proxy.ProxyFactory;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;

/**
 * 装载服务器动态生成的类。如果客户端不存在某些动态生成的类，则从服务器端进行装载。
 * 
 * @author zw
 * 
 */
public class ServerClassLoader extends ClassLoader {
	
	public ServerClassLoader (ClassLoader parent) {
		super(parent);
	}
	
	public Class<?> findClass (String name) throws ClassNotFoundException {
		LogWriter.debug("findClass... name", name);
		// 检查是否已经加载
		Class<?> loadedClass = findLoadedClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}
		
		// 只有动态生成的PO类使用本加载器处理！
		if (name.endsWith(IPOModifier.IPO_SUBFIX)) {
		    Proxy proxy = ProxyFactory.getInstance().getDefaultProxy();
		    Request request = new Request();
		    request.setMethodName("getClass");
		    request.setService(ServerClassService.class);
		    request.setParamTypes(new Class<?>[]{String.class});
		    request.setParams(new Object[]{name});
		    
		    Response res;
			try {
                // 
                Context context = new Context() {};
				res = proxy.invoke(request, context);
			} catch (Throwable e) {
				throw new ClassNotFoundException("远程调用时出现异常。", e);
			} finally {
                            // FIXME: onFinish();
                        }
		    byte[] b = (byte[])res.getReturnValue();
		    return defineClass(name, b, 0, b.length);
		} else {
			return getParent().loadClass(name);
		}
	}

}
