package com.xt.gt.jt.proc.pre;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;

import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.db.pm.IPersistence;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.bh.MethodParamInfo;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.sys.SystemConfigurationException;

/**
 * 通过处理方法的参数进行参数传递。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:"是否自动trim，校验（长度要考虑中文字节的问题）" 放在转换器和校验器中考虑
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-10
 */
public class MethodInputPrepareParams implements PrepareParams {

	/**
	 * 构建函数
	 */
	public MethodInputPrepareParams() {

	}

	/**
	 * 系统参数，不能作为某个服务的输入参数
	 */
	private static Set<String> systemParams = new HashSet<String>();

	static {
		// 不能反射的参数
		systemParams.add("bizHandler");
		systemParams.add("method");
	}

	public Class[] getParamClasses(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent req) {
		Class[] paramClasses = new Class[1];
		paramClasses[0] = dmInfo.getParamClazz();
		
		//如果用户未定义参数的类型，则采用方法的参数
		paramClasses[0] = getDefaultMethodType(bizHandler, dmInfo, 
				paramClasses[0]);
		LogWriter.debug("dmInfo.getParamClazz()", dmInfo.getParamClazz());
		return paramClasses;
	}

	public Object[] getParams(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent req) {
		Object[] params = null;
		// 给业务方法注入相应的数值
		params = injectValues(bizHandler, dmInfo, req);
		if (params == null || params.length != 1) {
			throw new SystemException("调用方法的方法必须有一个参数!");
		}
		return params;
	}

	/**
	 * 注入用户输入的值 首先查找用户自定义配置（类似于Web Service的描述文件）
	 * 然后，采用默认映射方式，即方法的声明里只有一个变量，如果是非基本类型的变量，要求传入的参数中只能有一个
	 * 非基本参数（bizHandler,method）， 如果方法没有输入参数，则自动映射类的参数 没有发现相应的类
	 * 
	 * @param bizHandler
	 * @param req
	 */
	private Object[] injectValues(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent req) {
		/**
		 * @todo 还存在值校验的问题没有解决
		 */
		Object[] params = null;

		// 参数类型
		Class paramType = dmInfo.getParamClazz();
		
        paramType = getDefaultMethodType(bizHandler, dmInfo, paramType);
		LogWriter.debug("injectValues class", paramType);

		// 这种方式只允许方法里有一个参数
		if (paramType.isPrimitive()) {
			throw new SystemException("参数的类型不能是原始类型");
		}

		// 只有实现了持久化接口的对象才改变其状态
		if (IPersistence.class.isAssignableFrom(paramType)) {
			ClassModifierFactory factory = ClassModifierFactory.getInstance();
			paramType = factory.modify(paramType);
		}

		//LogWriter.debug("injectValues param", paramType.getName());
		params = (Object[]) Array.newInstance(paramType, 1);

		// 如果参数类型是RequestEvent,则直接注入RequestEvent对象并返回
		if (paramType == RequestEvent.class
				|| RequestEvent.class.isAssignableFrom(paramType)) {
			params[0] = req;
			return params;
		}
		
//		 如果参数类型是HttpServletRequest,则直接注入HttpServletRequest对象并返回
		if (paramType == HttpServletRequest.class
				|| HttpServletRequest.class.isAssignableFrom(paramType)) {
			params[0] = req.getRequest();
			return params;
		}

		// 如果类不是基本类型，需要初始化参数
		if (!paramType.isPrimitive()) {
			// 参数对象
			Object paramValue = ClassHelper.newInstance(paramType);
			params[0] = paramValue;
		}

		String[] paramNames = req.getParameterNames();
		for (int i = 0; i < paramNames.length; i++) {
			String name = paramNames[i];
//			LogWriter.debug("injectValues req name", name);

			// 不要转入系统参数
			if (!systemParams.contains(name)) {
				// 数据类型
				Class type = ClassHelper.getFieldType(paramType, name);

				// 类中的属性不包含HTTP请求中含义的参数不进行处理
				if (type != null) {
					if (type.isArray()) {
						// 如果方法的参数是数组
						processArray(req, params[0], name, type);
					} else if (Collection.class.isAssignableFrom(type)) {
						// 处理对象集合的情况
						MethodParamInfo mpi = dmInfo.getParamInfo(name);
						LogWriter.debug("mpi", mpi);
						if (mpi != null && mpi.getCollectionClass() != null) {
							processJSonCollection(req, params[0], name, type, mpi.getCollectionClass());
						} else {
						    processStringCollection(req, params[0], name, type);
						}
					} else if (type == InputStream.class) {
						// 处理InputSteam类型
						processInputStream(req, params[0], name);
					} else {
						String value = req.getParameter(name);
						// 根据参数的类型对值进行转换
						Object convertedValue = ConvertUtils.convert(value,
								type);
						// 是普通的对象，复合类型（采用ONGL方式进行赋值）
						BeanHelper
								.copyProperty(params[0], name, convertedValue);
					}
				}
			}
		}

		return params;
	}

	private Class getDefaultMethodType(Object bizHandler, DealMethodInfo dmInfo, Class paramType) {
		//		如果用户未定义参数的类型，则采用方法的参数
		if (paramType == null) {
			Method method = ClassHelper.getMethod(bizHandler.getClass(), 
					dmInfo.getMethod());
			if (method.getParameterTypes() == null 
					|| method.getParameterTypes().length != 1) {
				throw new SystemConfigurationException("采用方法传入参数默认配置时" +
						"（METHOD_INPUT_PARAM）方法参数只能为一个。");
			}
			paramType = method.getParameterTypes()[0];
			dmInfo.setParamClazz(paramType);
		}
		return paramType;
	}

	/**
	 * 当输入参数是InputStream类型的时候,数组类型只能是整数
	 * 
	 * @param req
	 */
	private void processArray(RequestEvent req, Object target, String name,
			Class paramType) {
		Class elemClass = ClassHelper.getClassFromArray(paramType);
		String[] values = req.getParameterValues(name);
		if (values != null) {
			Object[] properties = (Object[]) Array.newInstance(elemClass,
					values.length);
			for (int i = 0; i < values.length; i++) {
				// 根据参数的类型对值进行转换
				Object convertedValue = ConvertUtils.convert(values[i],
						elemClass);
				properties[i] = convertedValue;
			}
			// 给集合类型赋值
			BeanHelper.copyProperty(target, name, properties);
		}
	}

	/**
	 * 当输入参数是集合类型的时候注入参数。注意，这个Colltion包含的只能是字符串类型。
	 * 
	 * @param req
	 */
	private void processStringCollection(RequestEvent req, Object target,
			String name, Class type) {
//		LogWriter.debug("processCollection", type);
//		LogWriter.debug("name", name);
		Collection coll = (Collection) ClassHelper.newInstance(type);
		String[] values = req.getParameterValues(name);
//		LogWriter.debug("values", values);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				coll.add(values[i]);
			}
		}
		// 给集合类型赋值
		BeanHelper.copyProperty(target, name, coll);
	}
	
	/**
	 * 当输入参数是集合类型的时候注入参数。注意，这个Colltion包含的只能是字符串类型。
	 * 
	 * @param req
	 */
	private void processJSonCollection(RequestEvent req, Object target,
			String name, Class type, Class collectionClass) {
		Collection coll = (Collection) ClassHelper.newInstance(type);
		String jsonValue = req.getParameter(name);  // 参数必须是JSON的字符串形式
		LogWriter.debug("jsonValue", jsonValue);
		if (StringUtils.isNotEmpty(jsonValue)) {
			JSONArray values = JSONArray.fromObject(jsonValue);
			for (Iterator iter = values.iterator(); iter.hasNext();) {
				JSONObject obj = (JSONObject) iter.next();
				Object value = ClassHelper.newInstance(collectionClass);  // 创建一个复合对象
				for (Iterator iterKeys = obj.keys(); iterKeys.hasNext();) {
					String propertyName = (String)iterKeys.next();
					BeanHelper.copyProperty(value, propertyName,obj.get(propertyName));
				}
				coll.add(value);
			}
		}
		// 给集合类型赋值
		BeanHelper.copyProperty(target, name, coll);
	}

	/**
	 * 当输入参数是InputStream类型的时候
	 * 
	 * @param req
	 * @param target
	 *            赋值的目标
	 */
	private void processInputStream(RequestEvent req, Object target, String name) {

		InputStream is = req.getInputStream(name);
//		LogWriter.debug("processInputStream name", name);
//		LogWriter.debug("processInputStream is", is);
		BeanHelper.copyProperty(target, name, is);
	}
}
