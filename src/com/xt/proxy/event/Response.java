package com.xt.proxy.event;

import java.io.Serializable;

public class Response implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -535593409515035698L;

	/**
	 * 服务处理后的返回值
	 */
	private Object returnValue;
	
	/**
	 * 服务对象
	 */
	private Object serviceObject;
	
//	/**
//	 * 用于存储不可序列化的值，并将其转换。比如：将InputStream转换为byte[]类型。
//	 */
//	private Map<String, Object> unserializedProperties;
	
	/**
	 * 返回的参数值（使得参数调用完成引用传递的功能），参数按照的方法参数的顺序排布
	 */
	private Object[] refParams;
	
//	/**
//	 * 服务器端出现的异常信息(暂时未使用，异常都是直接序列化的)
//	 */
//	private Throwable throwable;
	
	
	public Response () {
	}

	public Object[] getRefParams() {
		return refParams;
	}

	public void setRefParams(Object[] params) {
		this.refParams = params;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Object getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(Object serviceObject) {
		this.serviceObject = serviceObject;
	}
	

//	public Map<String, Object> getUnserializedProperties() {
//		return unserializedProperties;
//	}
//
//	public void setUnserializedProperties(Map<String, Object> unserializedProperties) {
//		this.unserializedProperties = unserializedProperties;
//	}
	
}
