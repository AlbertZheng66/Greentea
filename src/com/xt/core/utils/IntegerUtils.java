package com.xt.core.utils;

import com.xt.core.exception.BadParameterException;

public class IntegerUtils {
	
	/**
	 * 判断整数的正则表达式
	 */
	private final static String INTEGER_REGEXP_FORMAT = "(\\+|-)?\\d{1,10}";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public static int getInteger (Object value) throws BadParameterException {
		if (null == value) {
			throw new BadParameterException("数值格式为空且没有定义！");
		} else if (value instanceof Integer) {
			return ((Integer)value).intValue();
		} else if (value instanceof String) {
			String strValue = (String)value;
			//首先校验格式是否合法
			if (!RegExpUtils.generate(INTEGER_REGEXP_FORMAT, strValue).matches()) {
				throw new BadParameterException("不合法的数值格式");
			}
            
			//合法的参数格式
			return Integer.parseInt(strValue);			
		} else {
			throw new BadParameterException("不能识别或者是不合法的数值格式");
		}
	}
}
