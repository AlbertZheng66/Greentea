package com.xt.core.cm;


public class NewMethod {
	
	/**
	 * 方法的参数是否依赖于接口（即此方法为接口中定义的方法）
	 */
	private boolean byInterface = true;
	
	/**
	 * 新方法的返回值(默认为返回空)
	 */
	private Class returnType = void.class;
	
	/**
	 * 新方法的参数数组
	 */
	private Class[] paramsType;
	
	/**
	 * 方法抛出的异常
	 */
	private Class[] exceptionsType = null;
	
	private String name;
    
    private String body;
    
    public NewMethod () {
    	
    }

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class[] getParamsType() {
		return paramsType;
	}

	public void setParamsType(Class[] paramsType) {
		this.paramsType = paramsType;
	}

	public boolean isByInterface() {
		return byInterface;
	}

	public void setByInterface(boolean byInterface) {
		this.byInterface = byInterface;
	}

	public Class[] getExceptionsType() {
		return exceptionsType;
	}

	public void setExceptionsType(Class[] exceptionsType) {
		this.exceptionsType = exceptionsType;
	}

	public Class getReturnType() {
		return returnType;
	}

	public void setReturnType(Class returnType) {
		this.returnType = returnType;
	}
    
    
}
