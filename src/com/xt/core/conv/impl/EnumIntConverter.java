package com.xt.core.conv.impl;

import com.xt.core.conv.Converter;

public class EnumIntConverter <T extends Enum<T>> implements Converter<T, Integer> {

	public Integer convert(Class<T> targetClass, Class<Integer> sourceClass, T value) {
		if (value == null) {
			return null;
		}
		return value.ordinal();	
    }

}