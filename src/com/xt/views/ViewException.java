package com.xt.views;

import com.xt.core.exception.SystemException;

public class ViewException extends SystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1096047804069052528L;

	/**
     * 构造函数
     * 传入异常信息，设置默认的异常级别
     */
    public ViewException (String code)
    {
        super(code);
    }

    

    /**
     * 构造函数
     * 传入异常信息和异常类的实例，同时设置默认的异常级别
     */
    public ViewException (String code, Throwable cause)
    {
        super(code, cause);
    }

}
