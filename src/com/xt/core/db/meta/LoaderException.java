package com.xt.core.db.meta;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class LoaderException
    extends SystemException
{
    /**
     * 异常的默认代码
     */
    private static final String CODE = "???";

    public LoaderException(Exception ex)
    {
        super(CODE, ex);
    }

    public LoaderException(String code, Exception ex)
    {
        super(code, ex);
    }
}
