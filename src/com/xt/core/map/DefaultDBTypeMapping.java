package com.xt.core.map;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: 数据库类型映射类。此类定义了Java类型与数据库类型之间的映射关系。
 *                 定义关系可以在配置文件中找到，也可以采用默认的配置方式。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DefaultDBTypeMapping implements DBTypeMapping {
    /* (non-Javadoc)
	 * @see com.xt.core.map.DBTypeMapping#getJavaType(java.lang.String)
	 */
    public Class getJavaType (String dbType) {
    	if ("VARCHAR".equals(dbType)) {
    		return String.class;
    	} else if ("NUMBERIC".equals(dbType)) {
    		return double.class;
    	}  
    	return null;
    }
    
    /* (non-Javadoc)
	 * @see com.xt.core.map.DBTypeMapping#getDBType(java.lang.Class)
	 */
    public String getDBType (Class javaType) {
    	String type = null;
		if (String.class == javaType) {
			type = "VARCHAR";
		} else if (int.class == javaType) {
			type = "INTEGER";
		} else if (long.class == javaType) {
			type = "LONG";
		} else if (float.class == javaType) {
			type = "NUMBERIC";
		} else if (double.class  == javaType) {
			type = "NUMBERIC";
		}
		return type;
    }
    
    /* (non-Javadoc)
	 * @see com.xt.core.map.DBTypeMapping#getDBType(java.lang.String)
	 */
    public String getDBType(String name) {
		String type = null;
		if ("java.lang.String".equals(name)) {
			type = "VARCHAR";
		} else if ("int".equals(name)) {
			type = "INTEGER";
		} else if ("long".equals(name)) {
			type = "LONG";
		} else if ("float".equals(name)) {
			type = "NUMBERIC";
		} else if ("double".equals(name)) {
			type = "NUMBERIC";
		}
		return type;
	}
}
