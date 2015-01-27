package com.xt.core.conv.impl;

import com.xt.core.conv.Converter;
import com.xt.core.exception.SystemException;

public class IntEnumConverter<T extends Enum<T>> implements Converter<Integer, T> {

	public T convert(Class<Integer> sourceClass, Class<T> targetClass, Integer value) {
		if (value == null) {
			return null;
		}
		int ordinal = value.intValue();
		T[] values = targetClass.getEnumConstants();
		if (ordinal < 0 || ordinal >= values.length) {
			throw new SystemException(String.format("转换数值[%d]应大于等于 0 小于 %d。", ordinal, values.length));
		}
		return values[ordinal];
	}

}
