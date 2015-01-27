package com.xt.core.db.po;

import com.xt.core.exception.POException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 主键为空异常。进行删除，更新，更改操作时，如果主键为空，抛出此异常。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class NullPrimaryKeyException extends POException
{

    public NullPrimaryKeyException()
    {
    }

    public NullPrimaryKeyException(String exp)
    {
        super(exp);
    }

    /**
     * 显示给用户的错误信息代码。
     * @return 错误信息
     */
    public String getUserMessage()
    {
        return "00049";
    }
}
