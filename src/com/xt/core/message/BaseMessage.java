package com.xt.core.message;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 产生用户消息的基础类。用于用户操作后所产生的结果的错误信息或者提示信息的显示。
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class BaseMessage
{
    /**
     * 信息中参数的形式。（将用具体的变量替换掉）
     */
    public final static String PARAM_STYLE = "{?}";

    /**
     * 消息代码（唯一标识消息）
     */
    protected String code;

    /**
     * 信息中参数。如果一个信息中有动态参数需要得到，使用这个参数传入。
     */
    protected String[] messageParams;

    /**
     * 构造函数
     */
    public BaseMessage()
    {
    }

    /**
     * 得到信息代码
     * @return 消息代码
     */
    public String getCode()
    {
        return code;
    }

    /**
     * 设置信息代码
     * @param 消息代码
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    /**
     * 得到信息中参数集合
     * @return 信息中参数集合
     */
    public String[] getMessageParams()
    {
        return messageParams;
    }

    /**
     * 设置信息中参数集合
     * @param 信息中参数集合
     */
    public void setMessageParams(String[] messageParams)
    {
        this.messageParams = messageParams;
    }
}
