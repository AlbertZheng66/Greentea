package com.xt.core.map;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class BadFormatException
    extends SystemException
{
    public BadFormatException(Throwable t)
    {
        super("", t);
    }
}
