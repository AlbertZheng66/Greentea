package com.xt.core.exception;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 系统异常。由于程序的原因产生异常时抛出此异常。一般程序员不允许抛出此异常。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class SystemException
    extends BaseException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3701902413200653935L;

	/**
     * 构造函数
     * 传入异常信息，设置默认的异常级别
     */
    public SystemException (String message)
    {
        super(message);
    }

    /**
     * 构造函数
     * 传入异常信息和异常级别
     */
    public SystemException (String message, int level)
    {
        super(message, level);
    }

    /**
     * 构造函数
     * 传入异常信息和异常类的实例，同时设置默认的异常级别
     */
    public SystemException (String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * 构造函数
     */
    public SystemException ()
    {

    }
}
