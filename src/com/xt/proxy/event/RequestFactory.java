package com.xt.proxy.event;

import java.lang.reflect.Method;

import com.xt.core.exception.BadParameterException;
import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;

public class RequestFactory {

	public static Request createRequst(Class<? extends IService> service, String methodName) {
		if (service == null || methodName == null) {
			throw new BadParameterException("传入的参数服务类[" + service + "]和方法名称[" + methodName + "]都不能为空！");
		}
		Request req = new Request();
		req.setService(service);
		req.setMethodName(methodName);
		Method method = ClassHelper.getMethod(service, methodName);
		req.setParamTypes(method.getParameterTypes());
	
		return req;
	}

}
