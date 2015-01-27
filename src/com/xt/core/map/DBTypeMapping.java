package com.xt.core.map;

public interface DBTypeMapping {

	public abstract Class getJavaType(String dbType);

	public abstract String getDBType(Class javaType);

	public abstract String getDBType(String name);

}