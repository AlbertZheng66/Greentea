package com.xt.core.utils.dic;

import com.xt.core.exception.SystemException;

public class DictionaryException extends SystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1209038724255302741L;


	/**
     * 构造函数
     * 传入异常信息，设置默认的异常级别
     */
    public DictionaryException (String msg)
    {
        super(msg);
    }


    /**
     * 构造函数
     * 传入异常信息和异常类的实例，同时设置默认的异常级别
     */
    public DictionaryException (String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
