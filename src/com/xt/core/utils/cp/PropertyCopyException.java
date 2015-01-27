package com.xt.core.utils.cp;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class PropertyCopyException extends SystemException
{
    public PropertyCopyException()
    {
    	super("属性拷贝时发生异常！");
    }
    
    public PropertyCopyException(String code)
    {
    	super(code);
    }

}
