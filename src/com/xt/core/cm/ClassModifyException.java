package com.xt.core.cm;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: </p>
 * <p>Description: 类更改异常。
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ClassModifyException
    extends SystemException
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3316981072548119472L;

	public ClassModifyException (String code)
	    {
	       super(code);
	    }
	 
    public ClassModifyException (Throwable t)
    {
        super("ClassModifyException", t);
    }
}
