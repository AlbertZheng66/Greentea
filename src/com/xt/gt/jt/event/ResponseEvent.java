package com.xt.gt.jt.event;

import java.util.Set;

import com.xt.core.message.Messages;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface ResponseEvent
{
    /**
     * 设置属性，属性的作用域在一次请求连接内保持。一次请求之外，不保证此属性存在。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setAttribute (String name, int value);

    /**
     * 设置属性，属性的作用域在一次请求连接内保持。一次请求之外，不保证此属性存在。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setAttribute (String name, long value);

    /**
     * 设置属性，属性的作用域在一次请求连接内保持。一次请求之外，不保证此属性存在。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setAttribute (String name, float value);

    /**
     * 设置属性，属性的作用域在一次请求连接内保持。一次请求之外，不保证此属性存在。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setAttribute (String name, double value);

    /**
     * 设置属性，属性的作用域在一次请求连接内保持。一次请求之外，不保证此属性存在。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setAttribute (String name, Object value);

    /**
     * 设置属性，属性的作用域在整个会话期间内保持。
     * @param name 属性名称
     * @param value 属性的值
     */
    public void setSessionAttribute (String name, Object value);

    /**
     * 设置返回的消息集合。当事务处理出现异常时，应使用此方法返回消息。
     * @param messages 消息集合
     */
    public void setMessages (Messages messages);

    /**
     * 返回消息集合。当事务处理出现异常时，应使用此方法返回消息。
     * @param messages 消息集合
     */
    public Messages getMessages ();

    /**
     * 返回所有的request作用域的属性名称（Key值）。
     * @return 所有的request作用域的名称（Key值）
     */
    public Set getAttributeNames ();

    /**
     * 返回所有的session作用域的名称（Key值）。
     * @return 所有的session作用域的名称（Key值）
     */
    public Set getSessionAttributeNames ();

    /**
     * 返回指定名称的request作用域的属性值。
     * @param name 属性名称
     * @return 指定名称的request作用域的属性值
     */
    public Object getValue (String name);

    /**
     * 返回指定名称的session作用域的属性值。
     * @param name 属性名称
     * @return 指定名称的session作用域的属性值
     */
    public Object getSessionValue (String name);

    /**
     * 去除指定的session作用域的某个属性及其对应的值。
     * @param name 属性的名称
     */
    public void removeSessionAttributes (String name);

}
