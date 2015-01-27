package com.xt.proxy.impl.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.proxy.event.Request;

/**
 * 处理业务方法
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-24
 */
public class RichRequestProcessor {

	public Object process(Request request) {
		// 实例化的类，并得到方法参数的类型
		Object bizHandler = ClassHelper.newInstance(request.getService());

		Object ret = null;
		try {
			// 业务逻辑方法
			Method bizMethod = request.getService().getDeclaredMethod(
					request.getMethodName(), request.getParamTypes());

			if (null == bizMethod) {
				throw new SystemException("未发现合适的调用方法");
			}

			ret = bizMethod.invoke(bizHandler, request.getParams());
		} catch (IllegalAccessException e) {
			throw new SystemException("非法存取异常！", e);
		} catch (InvocationTargetException e) {
			throw new SystemException("非法调用异常！", e);
		} catch (SecurityException e) {
			throw new SystemException("业务处理方法不允许调用！");
		} catch (NoSuchMethodException e) {
			throw new SystemException("业务处理方法不存在！");
		} catch (IllegalArgumentException e) {
			throw new SystemException("非法调用参数异常！", e);
		}
		LogWriter.debug("after method invoke.....");
		return ret;
	}

	/**
	 * 校验请求是否正确!
	 * 
	 * @param request
	 */
	public void validate(Request request) {

	}

}
