package com.xt.core.conv.impl;

import java.lang.reflect.Field;

import com.xt.core.conv.Converter;
import com.xt.core.exception.SystemException;

public class EnumStringConverter<T extends Enum<T>> implements Converter<T, String> {

	public String convert(Class<T> sourceClass, Class<String> targetClass, T value) {
		if (value == null) {
			return null;
		}
		
		// 如果定义了缩写，首先使用缩写，否则使用名称。
		try {
			Field field = sourceClass.getField(value.name());
			Ab ab = field.getAnnotation(Ab.class);
			if (ab != null) {
				return ab.value();
			} else {
				return value.name();
			}
		} catch (Exception e) {
			throw new SystemException(String.format("数[%s]在枚举类[%s]中未定义。", value, targetClass.getName()));
		}
	}

}