package com.xt.gt.jt.screen.template;

import java.util.HashMap;

/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: 根据请求连接中的参数（handler和command）来确定对应的屏幕定义，
 * 在配置文件中定义这种映射关系。使用这种方式定义就不能再使用flowHandler的方式定义，
 * 即：只可取其一。一般情形下：handler下面可以嵌套command，如果请求连接的command
 * 参数不能匹配任何屏幕定义，则采用handler的默认定义，即handler标签的screen属性。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

/**
 * 
 */
public class RequestParamMapping
{
    /**
     * 请求连接的参数的名称
     */
    private String name;

    /**
     * 请求连接的参数的值
     */
    private String value;

    /**
     * 对应于请求连接的参数的值的屏幕定义
     */
    private String screen;

    /**
     * 子参数；请求连接的下一级参数。
     */
    private HashMap subParams;

    public RequestParamMapping()
    {
    }

    public RequestParamMapping(String name, String value, String screen)
    {
        this.name = name;
        this.value = value;
        this.screen = screen;
    }

    public HashMap getSubParams()
    {
        return subParams;
    }
    /**
     * 增加子参数
     * @param param 子参数
     */
    public void addSubParam (String value, RequestParamMapping param) {
        if (this.subParams == null) {
            subParams = new HashMap();
        }
        subParams.put(value, param);
    }

    public String toString ()
    {
        return "[RequestParamMapping: value=" + value +
            ", screen=" + screen +
            ", subParams=" + subParams +
            "]";
    }

    public String getValue()
    {
        return value;
    }
    public void setSubParams(HashMap subParams)
    {
        this.subParams = subParams;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getScreen()
    {
        return screen;
    }
    public void setScreen(String screen)
    {
        this.screen = screen;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}
