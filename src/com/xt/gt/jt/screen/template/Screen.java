package com.xt.gt.jt.screen.template;

import java.util.HashMap;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class Screen
    implements java.io.Serializable
{

    private String name;
    private HashMap parameters;

    public Screen (String name, HashMap parameters)
    {
        this.name = name;
        this.parameters = parameters;
    }

    public HashMap getParameters ()
    {
        return parameters;
    }

    public Parameter getParameter (String key)
    {
        return (Parameter) parameters.get(key);
    }

    public String toString ()
    {
        return "[Screen: name=" + name + ", parameters=" + parameters + "]";
    }
}
