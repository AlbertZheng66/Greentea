package com.xt.core.conv;

public interface BaseConverter<T> {
    /**
     * 转换为字符串时采用的统一编码格式。特别是数组等情况，需要明确指定编码。
     */
    public static final String ENCODING = "UTF-8";

	
	public Class<T> getConvertedClass();
	
	public T convert (String value);
	
	public String convertToString(T obj);

}
