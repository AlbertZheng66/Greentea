package com.xt.gt.jt.screen.template;

import java.util.HashMap;

/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class URLMapping
    implements java.io.Serializable
{
    /* 请求连接 */
    private String url;

    /* 是否使用流处理器 */
    private boolean useFlowHandler = false;

    /* 流处理器类 */
    private String flowHandler = null;

    /* 处理结果集 */
    private HashMap resultMappings;

    /* 屏幕名称 */
    private String screen;

    /**
     * 参数映射
     */
    private HashMap paramMappings;

    /**
     * 使用屏幕流处理器的情况下使用此构造函数
     * @param url 请求连接
     * @param screen 屏幕名称
     * @param bizHandler 事务处理器的类名称
     * @param useFlowHandler 是否使用屏幕流处理器
     * @param flowHandler 屏幕流处理器的类
     * @param resultMappings 屏幕流处理后的结果集
     */
    public URLMapping (String url, String screen, boolean useFlowHandler, String flowHandler,
                       HashMap resultMappings)
    {
        this.url = url;
        this.flowHandler = flowHandler;
        this.useFlowHandler = useFlowHandler;
        this.resultMappings = resultMappings;
        this.screen = screen;
    }

    public URLMapping (String url, String screen, HashMap paramMappings)
    {
        this.url = url;
        this.screen = screen;
        this.paramMappings = paramMappings;
    }

    public boolean useFlowHandler ()
    {
        return useFlowHandler;
    }

    public String getFlowHandler ()
    {
        return flowHandler;
    }

    public String getScreen ()
    {
        return screen;
    }

    public String getResultScreen (String key)
    {
        if (resultMappings != null)
        {
            return (String)resultMappings.get(key);
        }
        else
        {
            return null;
        }
    }

    public HashMap getResultMappings ()
    {
        return resultMappings;
    }

    public String toString ()
    {
        return "[URLMapping: url=" + url + ", screen=" + screen + ", useFlowHandler="
            + useFlowHandler + ", flowHandler=" + flowHandler + ", paramMappings=" + paramMappings
            + ", resultMappings=" + resultMappings + "]";
    }

    public HashMap getParamMappings ()
    {
        return paramMappings;
    }

    public boolean isUseFlowHandler ()
    {
        return useFlowHandler;
    }

}
