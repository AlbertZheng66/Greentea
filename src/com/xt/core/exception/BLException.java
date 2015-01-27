package com.xt.core.exception;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 事务逻辑异常。处理用户事务逻辑产生异常时抛出此异常。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class BLException extends BaseException
{
    /**
     * 构造函数
     * 异常信息代码
     */
    public BLException(String code)
    {
        super(code);
    }

    /**
     * 构造函数
     * 异常信息代码
     */
    public BLException(String code, Throwable cause)
    {
        super(code, cause);
    }

    /**
     * 显示给用户的错误信息。
     * @return 错误信息
     */
    public String getUserMessage()
    {
        return this.code;
    }
}
