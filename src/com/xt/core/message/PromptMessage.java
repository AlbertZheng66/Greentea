package com.xt.core.message;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 提示信息。用户操作成功后，给用户的提示信息，比如保存成功后的提示信息。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class PromptMessage extends BaseMessage
{
    /**
     * 构造函数
     */
    public PromptMessage()
    {
    }

    /**
     * 传入提示信息
     * @param code 提示信息
     */
    public PromptMessage(String code)
    {
        this.code = code;
    }
}
