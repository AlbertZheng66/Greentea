package com.xt.core.app.init;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:系统复位时产生的异常需要通过此方法处理。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ResetException
    extends SystemException
{
    public ResetException (String code)
    {
        super(code);
    }

    public ResetException (String code, int level)
    {
        super(code, level);
    }

    public ResetException (String code, Throwable cause)
    {
        super(code, cause);
    }

    public ResetException (Throwable cause)
    {
        super("00095", cause);
    }

    public ResetException ()
    {
        super("00095");
    }
}
