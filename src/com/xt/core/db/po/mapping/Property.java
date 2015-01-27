/**
 * 
 */
package com.xt.core.db.po.mapping;

/**
 * <p>
 * Title: XT框架-持久化部分
 * </p>
 * <p>
 * Description: 持久化对象的属性。
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
 * @date 2006-4-15
 */

public class Property {
	
	/**
	 * 被排除的列，即此列不需要持久化
	 */
	private boolean exclude;
	
	private String name;

	private String columnName;

	private boolean notNull;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public boolean isExclude() {
		return exclude;
	}

	public void setExclude(boolean exclude) {
		this.exclude = exclude;
	}

}
