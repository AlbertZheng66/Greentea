package com.xt.core.exception;


/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class BadParameterException
        extends SystemException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadParameterException(String message)
    {
        super(message);
    }
	
    public BadParameterException(String message, Throwable t)
    {
        super(message, t);
    }
}
