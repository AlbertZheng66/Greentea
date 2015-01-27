package com.xt.core.utils.cp;

/**
 * <p>
 * Title: XT框架-核心逻辑部分.
 * </p>
 * <p>
 * Description: 属性复制接口，用于快速的给一个对象赋值，以及对象直接进行快速的属性复制工作。
 * 一般情况下，此接口是通过默认的方式织入到对象中的。
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
 */

public interface PropertyWriter {
	/**
	 * 设置一个属性。
	 * @param propertyName
	 * @param value
	 * @throws PropertyCopyException
	 */
	public void setProperty(String propertyName, Object value)
			throws PropertyCopyException;

	/**
	 * 复制一个类型相同的对象。将输入对象的值完全复制到本对象中。
	 * @param value 如果对象为空，或者类型不相符，抛出属性复制异常。
	 * @throws PropertyCopyException
	 */
	public void duplicate(Object value) throws PropertyCopyException;
}
