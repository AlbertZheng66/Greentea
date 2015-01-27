package com.xt.test.conf;

import org.jdom.Element;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 配置接口，用于装载代码对应的属性值。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface IConfig
{
    public Object load (String id, Element value)
        throws ConfigurationException;
}
