package com.xt.gt.jt.bh.mapping;

import java.lang.reflect.Method;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.StringUtils;
import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 缺省的业务逻辑和服务类的映射关系类。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  缺省的映射关系类。映射规则是：将路径名称转换为包名称；
 * 将请求BizHander的名称转换为服务类的名称，并在后面加上“Service”后缀
 * （可在配置文件中定义，默认为“Service”）。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-12
 */
public class DefaultBizMapping implements BizMapping {

	private String subfix = SystemConfiguration.getInstance().readString(
			SystemConstants.SERVICE_SUBFIX, "Service");

	/**
	 * 类的基础路径名称，从配置文件中读取
	 */
	private String basePackage = SystemConfiguration.getInstance().readString(
			SystemConstants.BASE_PACKAGE, "com.xt.gt");

	public DefaultBizMapping() {
	}

	/**
	 * packageName的格式为xt/account bizHandler不能为空
	 */
	public BizHandlerInfo getBizHandlerInfo(String packageName,
			String bizHandler) {
		StringBuffer fullClassName = new StringBuffer();
		if (basePackage != null && basePackage.trim().length() > 0) {
			fullClassName.append(basePackage).append(".");
		}

		//检查还不是很严格，比如：“packageName”后面有没有“/”,"/"是不是重复等情况
		if (packageName != null && packageName.trim().length() > 0) {
			fullClassName.append(packageName.replace('/', '.')).append(".");
		}

		fullClassName.append(StringUtils.capitalize(bizHandler));
		
		//增加事务处理类的后缀
		fullClassName.append(subfix);

		BizHandlerInfo bizInfo = new BizHandlerInfo();
		bizInfo.setName(packageName + "." + bizHandler);
		try {
			bizInfo.setHandlerClass(Class.forName(fullClassName.toString()));
		} catch (ClassNotFoundException e) {
			throw new SystemException("没有找到类[" + fullClassName + "]");
		}

		return bizInfo;
	}

	/**
	 * 前置条件:bizHandlerInfo和methodName都不能为空
	 */
	public DealMethodInfo getDealMethodInfo(BizHandlerInfo bizHandlerInfo,
			String methodName) {

		// 业务处理类
		Class bizClass = bizHandlerInfo.getHandlerClass();

		DealMethodInfo methodInfo = new DealMethodInfo();
		//方法的名称和对应的处理方法同名
		methodInfo.setName(methodName);
		methodInfo.setMethod(methodName);

		// **事务处理逻辑类中的方法不要重载**
		Method method = ClassHelper.getMethod(bizClass, methodName);

		if (method == null) {
			throw new SystemException("类[" + bizClass.getName() + "]未定义方法["
					+ methodName + "]!");
		}

		//采用默认方式可以定义一个参数或者0个参数
		Class[] types = method.getParameterTypes();
		if (types != null && types.length == 1) {
			methodInfo.setParamType(DealMethodInfo.METHOD_INPUT_PARAM);
			methodInfo.setParamClazz(types[0]);
		} else if (types == null || types.length == 0){
			methodInfo.setParamType(DealMethodInfo.METHOD_INPUT_PARAM_NULL);
		} else {
			throw new SystemException("类[" + bizClass.getName() + "]方法["
					+ methodName + "]定义了多余一个参数,不能使用默认的映射方式");
		}
		LogWriter.debug ("methodInfo", methodInfo);
		

		return methodInfo;
	}

}
