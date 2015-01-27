package com.xt.gt.jt.proc.pre;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.ConvertUtils;

import com.xt.core.exception.ServiceException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.bh.MethodParamInfo;
import com.xt.gt.jt.event.RequestEvent;

public class CustomizedParams implements PrepareParams {
	// 需要注入的参数类型
	Class[] classes = null;

	public Class[] getParamClasses(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent) {
		load(bizHandler, dmInfo);

		return classes;
	}

	public Object[] getParams(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent) {
		load(bizHandler, dmInfo);

		Object[] params = new Object[classes.length];
		for (int i = 0; i < classes.length; i++) {
			Class clazz = classes[i]; // 参数的类型
			
			// 如果参数类型是RequestEvent,则直接返回RequestEvent对象
			if (RequestEvent.class.isAssignableFrom(clazz)) {
				params[i] = requestEvent;
			} else {
				MethodParamInfo mpi = (MethodParamInfo) dmInfo.getParamInfos()
						.get(i);
				String name = mpi.getName();
				int scope = mpi.getScope();
				Object value = getValue(requestEvent, name, scope);

				if (value instanceof String) {
					// 根据参数的类型对值进行转换
					value = ConvertUtils.convert((String) value, clazz);
				}
				params[i] = value;
			}
		}
		return params;
	}

	private Object getValue(RequestEvent requestEvent, String name, int scope) {
		Object ret = requestEvent.getAttribute(name, scope);
		LogWriter.debug("name", name);
		LogWriter.debug("scope", scope);
		LogWriter.debug("value", ret);
		return ret;
	}

	/**
	 * 根据方法的名称得到方法的参数类型。 通过方法的可见性、参数的个数和名称， 即如果一个方法是公共的，且其参数个数与配置文件的个数相同，名称也相同，
	 * 则认为找到相同的方法。如果未找到合适的方法，则抛出ServiceException异常。
	 * 
	 * @param bizHandler
	 *            事务处理实例
	 * @param dmInfo
	 *            处理方法信息
	 */
	private void load(Object bizHandler, DealMethodInfo dmInfo) {
		// 避免重复加载
		if (classes != null) {
			return;
		}
		int count = dmInfo.getParamInfos().size(); // 参数的个数
		Method[] methods = bizHandler.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// 通过方法的可见性、参数的个数和名称
			if (method.getModifiers() == Modifier.PUBLIC
					&& method.getName().equals(dmInfo.getMethod())
					&& method.getParameterTypes().length == count) {
				classes = method.getParameterTypes();
				break;
			}
		}
		if (classes == null) {
			throw new ServiceException("未发现合适的方法[" + dmInfo.getName()
					+ "],参数个数[" + count + "]");
		}
	}

}
