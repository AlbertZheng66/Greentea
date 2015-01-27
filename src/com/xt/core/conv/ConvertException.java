package com.xt.core.conv;

import com.xt.core.exception.SystemException;

public class ConvertException extends SystemException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2800103237936951584L;

	/**
     * 构造函数
     * 传入异常信息
     */
    public ConvertException (String msg)
    {
        super(msg);
    }

    /**
     * 构造函数
     * 传入异常信息和异常类的实例，同时设置默认的异常级别
     */
    public ConvertException (String msg, Throwable cause)
    {
        super(msg, cause);
    }


}
