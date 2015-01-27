package com.xt.core.message;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:错误信息。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ErrorMessage
    extends BaseMessage
{
    /**
     * 构造函数
     */
    public ErrorMessage ()
    {
    }

    /**
     * 传入异常信息
     * @param code 错误信息
     */
    public ErrorMessage (String code)
    {
        this.code = code;
    }
}
