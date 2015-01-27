package com.xt.gt.jt.screen.template;

import java.io.Serializable;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class Parameter
    implements Serializable
{

    private String key;
    private String value;
    private boolean direct;

    public Parameter (String key, String value, boolean direct)
    {
        this.key = key;
        this.value = value;
        this.direct = direct;
    }

    public boolean isDirect ()
    {
        return direct;
    }

    public String getKey ()
    {
        return key;
    }

    public String getValue ()
    {
        return value;
    }

    public String toString ()
    {
        return "[Parameter: key=" + key + ", value=" + value + ", direct=" +
            direct + "]";
    }
}
