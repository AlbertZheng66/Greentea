package com.xt.core.utils.cp;

/**
 * <p>
 * Title: XT框架-核心逻辑部分-属性读取接口.
 * </p>
 * <p>
 * Description:属性读取接口是为了快速读取对象的属性值而设计的。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public interface PropertyReader {
	
	/**
	 * 根据属性的名称，返回属性的值。如果属性不存在，抛出属性复制异常。
	 * @param propertyName 属性名称
	 * @return
	 * @throws PropertyCopyException 属性为空，或者属性不存在的情况下抛出
	 */
	public Object getPropertyValue(String propertyName) throws PropertyCopyException;

	/**
	 * 返回对象本身所有的属性值，包括其父类的对象值
	 * @return
	 * @throws PropertyCopyException
	 */
	public String[] getPropertyNames() throws PropertyCopyException;

	/**
	 * 返回属性的类型。
	 * @param propertyName
	 * @return
	 * @throws PropertyCopyException
	 */
	public Class getPropertyClass(String propertyName)
			throws PropertyCopyException;
}
