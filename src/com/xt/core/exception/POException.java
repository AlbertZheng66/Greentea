package com.xt.core.exception;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 持久对象（数据库）异常。用于处理数据库操作引发的异常，比如：将SQLException转换
 *                 成POException异常，等。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class POException extends SystemException
{
    /**
     * 构造函数
     */
    public POException()
    {
    }

    /**
     * 构造函数
     * 传入异常信息
     */
    public POException(String message)
    {
        super(message);
    }

//    /**
//     * 构造函数
//     * 传入异常信息，设置默认的异常级别
//     */
//    public POException(String message, int level)
//    {
//        super(message, level);
//    }

    /**
     * 构造函数
     * 传入异常信息和异常类的实例，同时设置默认的异常级别
     */
    public POException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
