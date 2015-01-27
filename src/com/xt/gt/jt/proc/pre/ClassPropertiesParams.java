package com.xt.gt.jt.proc.pre;

import org.apache.commons.beanutils.ConvertUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.event.RequestEvent;

public class ClassPropertiesParams implements PrepareParams {

	/**
	 * 通过类型注入则无参数传入
	 */
	public Class[] getParamClasses(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent) {
		return null;
	}

	/**
	 * 通过类型注入则无参数传入
	 */
	public Object[] getParams(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent) {
		// 业务处理类型
		Class bizHandlerClass = bizHandler.getClass();
        String[] paramNames = requestEvent.getParameterNames();
		for (int i =0; i < paramNames.length; i++) {
			String name = paramNames[i];
			LogWriter.debug("injectValues req name", name);

			// 数据类型
			Class type = ClassHelper.getFieldType(bizHandlerClass, name);

			// 类中的属性不包含HTTP请求中含义的参数不进行处理
			if (type != null) {
				String value = requestEvent.getParameter(name);

				// 根据参数的类型对值进行转换
				Object convertedValue = ConvertUtils.convert(value, type);

				// 如果方法是数组，集合，复合类型等情况，还需要考虑，
				// 是否自动trim，校验（长度要考虑中文字节的问题）
				BeanHelper.copyProperty(bizHandler, name, convertedValue);
			}
		}

		return null;
	}

}
