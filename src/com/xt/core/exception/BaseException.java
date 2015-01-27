package com.xt.core.exception;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 所有异常信息的基类，系统中所有的异常必须继承此类。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class BaseException
    extends RuntimeException
{
    /**
     * 异常代码。
     */
    protected String code;

//    protected String[] codeParams;

    /**
     * 异常级别。
     */
    protected int level;

    /**
     * 致命异常。这是最严重的异常级别，可能导致系统的崩溃，数据库死机等情形。
     */
    public final static int FATAL = 0;

    /**
     * 错误级异常。这种异常是因为程序设计的缺陷，Bug等引起的异常，应当由程序员修改程序以避免再次出现
     * 此异常。
     */
    public final static int ERROR = 1;

    /**
     * 警告级异常。由于用户非法的输入（未通过校验的输入）或者是未经授权的访问而产生的异常，一般情形
     * 下要求用户重新输入，或者重新登录授权。
     */
    public final static int WARNING = 2;

    /**
     * 提示信息级。关键的或者重要的操作时提请用户注意的地方。（是否加在这里还有待商榷？）
     */
    public final static int INFO = 3;

    /**
     * 构造函数
     */
    public BaseException ()
    {
        super();
    }

    /**
     * 构造函数
     * 传入异常信息，默认的异常级别是ERROR
     */
    public BaseException (String code)
    {
        super(code);
        this.code = code;
        this.level = ERROR;
    }

    /**
     * 构造函数
     * 传入异常信息和异常级别
     */
    public BaseException (String code, int level)
    {
        super();
        this.code = code;
        this.level = level;
    }

    /**
     * 构造函数
     * 传入异常信息和异常类的实例，默认的异常级别是ERROR
     */
    public BaseException (String code, Throwable cause)
    {
    	super(code);
    	initCause(cause);
        this.code = code;
        this.level = ERROR;
    }

    /**
     * 显示给用户的错误信息。
     * @return 错误代码。
     */
    public String getUserMessage ()
    {
        return code;
    }
}
