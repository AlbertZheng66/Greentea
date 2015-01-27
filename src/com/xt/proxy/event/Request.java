package com.xt.proxy.event;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.Pair;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 此类封装了一次请求的相关参数。只供程序内部使用，不建议项目中使用此类的任何方法。
 * @author albert
 *
 */
public class Request implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5387035208937036009L;

	/**
	 * 标识唯一的Session，用于安全验证
	 */
	private String sessionId;
	
	/**
	 * 服务类型
	 */
	private Class<? extends IService> service;
	
	/**
	 * 服务对象
	 */
	private IService serviceObject;
	
    
	/**
	 * 调用方法的名称
	 */
	private String methodName;
	
	/**
	 * 调用方法的参数类型
	 */
	private Class<?>[] paramTypes;
	
	/**
	 * 调用方法的参数值
	 */
	private Object[] params;
	
	/**
	 * 当前对象的属性
	 */
	private Pair<Serializable>[] properties;
	
	/**
	 * 默认构造函数
	 */
	public Request () {
	}
	

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}

	public Class<? extends IService> getService() {
		return service;
	}

	public void setService(Class<? extends IService> service) {
		this.service = service;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public IService getServiceObject() {
		// 既非接口，也既非抽象类，则完成自动实例化
		if (serviceObject == null 
				&& !service.isInterface() 
				&& !Modifier.isAbstract(service.getModifiers())) {
			serviceObject = ClassHelper.newInstance(service);
		}
		return serviceObject;
	}


	public void setServiceObject(IService serviceObject) {
		this.serviceObject = serviceObject;
	}


	public Pair<Serializable>[] getProperties() {
		return properties;
	}


	public void setProperties(Pair<Serializable>[] properties) {
		this.properties = properties;
	}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	
}
