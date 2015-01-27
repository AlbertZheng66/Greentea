package com.xt.test.conf;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 配置文件异常。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ConfigurationException
    extends SystemException
{
    public ConfigurationException (String code, Exception ex)
    {
        super(code, ex);
    }
}
