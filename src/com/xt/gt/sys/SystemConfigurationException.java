package com.xt.gt.sys;

import com.xt.core.exception.SystemException;

/**
 * 系统配置异常。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  当读取的系统参数的类型或者参数装载不符合要求时将抛出此异常。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-17
 */
public class SystemConfigurationException extends SystemException {
    
    private static final long serialVersionUID = -4906190042008682980L;

    public SystemConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemConfigurationException(String message) {
        super(message);
    }
    
	
}
