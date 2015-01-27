package com.xt.core.conv.impl;

import java.lang.reflect.Field;

import com.xt.core.conv.Converter;
import com.xt.core.exception.SystemException;

public class StringEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    public T convert(Class<String> sourceClass, Class<T> targetClass, String value) {
        if (value == null) {
            return null;
        }
        T[] values = targetClass.getEnumConstants();
        if (values == null) {
            return null;
        }
        for (T enumValue : values) {
            if (value.equals(enumValue.name()) || value.equals(String.valueOf(enumValue.ordinal()))) {
                return enumValue;
            }
        }

        // 可能是缩写
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            Ab ab = field.getAnnotation(Ab.class);
            if (ab != null) {
                if (value.equals(ab.value())) {
                    return Enum.valueOf(targetClass, field.getName());
                }
            }
        }

        throw new SystemException(String.format("值[%s]在枚举类[%s]没有对应的名称。", value, targetClass.getName()));
    }
}