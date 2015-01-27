package com.xt.core.conv;

public interface Converter<S, T> {

    
	public T convert (Class<S> sourceClass, Class<T> targetClass, S value);

}
