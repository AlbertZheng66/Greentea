package com.xt.core.message;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:  所有消息的集合，用于存放由服务器端产生的所有的消息。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class Messages
{
    /*
     * 存放所有的消息。多数情况下只有一个错误信息，所以初始空间设定为1。
     */
    private List messages = new ArrayList(1);

    /**
     * 构造函数
     */
    public Messages ()
    {
    }

    /**
     * 构造函数
     * @param message
     */
    public Messages (BaseMessage message)
    {
        addMessage(message);
    }

    /**
     * 在消息集合中增加一条消息。
     * @param message 一条消息
     */
    public void addMessage (BaseMessage message)
    {
        messages.add(message);
    }

    /**
     * 得到信息集合
     * @return 消息集合
     */
    public List getMessages ()
    {
        return messages;
    }

    /**
     * 传入信息集合
     * @param messages 消息集合
     */
    public void setMessages (ArrayList messages)
    {
        this.messages = messages;
    }
}
