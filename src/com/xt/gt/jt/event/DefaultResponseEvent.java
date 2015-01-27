package com.xt.gt.jt.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xt.core.message.Messages;
import com.xt.core.message.PromptMessage;

/**
 * 缺省的响应处理事件。实现了ResponseEvent类中的一些基本的方法。 
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-7-23
 */
public class DefaultResponseEvent
    implements ResponseEvent
{
    /**
     * 作用域是request的属性集合。主键是属性的名称，对应的值是属性的值。
     */
    private Map requestAttributes;

    /**
     * 作用域是session的属性集合。主键是属性的名称，对应的值是属性的值。
     */
    private Map sessionAttributes;

    /**
     * 业务处理逻辑返回的消息的集合
     */
    private Messages messages;

    public DefaultResponseEvent ()
    {
    }

    /**
     * 构建函数
     * @param messageCode String 消息编码
     */
    public DefaultResponseEvent (String messageCode)
    {
        setMessage(messageCode);
    }

    /**
     * 可以返回一个名值对属性的构建函数
     * @param attributeName String 属性名称
     * @param value 属性的值
     */
    public DefaultResponseEvent (String attributeName, Object value)
    {
        setAttribute(attributeName, value);
    }

    /* 实现父接口 */
    public void setAttribute (String name, Object value)
    {
        if (requestAttributes == null)
        {
            requestAttributes = new HashMap();
        }
        this.requestAttributes.put(name, value);
    }

    public void setAttribute (String name, int value)
    {
        setAttribute(name, new Integer(value));
    }

    /*
     * 将真值转换成字符串“true”，将真值转换成字符串“false”
     */
    public void setAttribute (String name, boolean value)
    {
        setAttribute(name, String.valueOf(value));
    }

    public void setAttribute (String name, float value)
    {
        setAttribute(name, new Float(value));
    }

    public void setAttribute (String name, double value)
    {
        setAttribute(name, new Double(value));
    }

    public void setAttribute (String name, char value)
    {
        setAttribute(name, String.valueOf(value));
    }

    public void setAttribute (String name, long value)
    {
        setAttribute(name, new Long(value));
    }

    /* 实现父接口 */
    public void setSessionAttribute (String name, Object value)
    {
        if (sessionAttributes == null)
        {
            sessionAttributes = new HashMap();
        }
        this.sessionAttributes.put(name, value);
    }

    public void setMessages (Messages messages)
    {
        this.messages = messages;
    }

    /**
     * 设置返回的消息,在只返回<strong>一条提示</strong>消息时,使用此方法.方法默认的消息类型是:提示消息.
     * @param code String 消息的代码
     */
    public void setMessage (String code)
    {
        if (messages == null)
        {
            messages = new Messages();
        }
        PromptMessage pm = new PromptMessage(code);
        messages.addMessage(pm);
    }

    /* 实现父接口 */
    public Messages getMessages ()
    {
        return messages;
    }

    /* 实现父接口 */
    public Set getAttributeNames ()
    {
        Set names = null;
        if (requestAttributes != null)
        {
            names = requestAttributes.keySet();
        }
        return names;
    }

    /* 实现父接口 */
    public Set getSessionAttributeNames ()
    {
        Set ret = null;
        if (sessionAttributes != null)
        {
            ret = sessionAttributes.keySet();
        }
        return ret;
    }

    /* 实现父接口 */
    public Object getValue (String name)
    {
        Object ret = null;
        if (requestAttributes != null)
        {
            ret = this.requestAttributes.get(name);
        }
        return ret;
    }

    /* 实现父接口 */
    public Object getSessionValue (String name)
    {
        Object ret = null;
        if (sessionAttributes != null)
        {
            ret = this.sessionAttributes.get(name);
        }
        return ret;
    }

    /* 实现父接口 */
    public void removeSessionAttributes (String name)
    {
        //未完成
    }

    public void setSessionAttributes (Map sessionAttributes)
    {
        this.sessionAttributes = sessionAttributes;
    }

    public void setRequestAttributes (Map requestAttributes)
    {
        this.requestAttributes = requestAttributes;
    }
}
