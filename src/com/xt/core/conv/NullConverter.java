package com.xt.core.conv;

public class NullConverter<S, T> implements Converter<S, T> {

	@SuppressWarnings("unchecked")
	public T convert(Class<S> sourceClass, Class<T> targetClass, S value) {
		return (T)value;
	}
}
