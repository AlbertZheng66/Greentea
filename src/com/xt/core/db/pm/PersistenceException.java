package com.xt.core.db.pm;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: XT-框架</p>
 * <p>Description: 当持久化发生异常时，抛出此类。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class PersistenceException extends SystemException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7793279339619818764L;
	/**
     * 默认异常代码
     */
    public final static String DEFAULT_CODE = "";
    
    public PersistenceException(String code)
    {
        super(code);
    }

    public PersistenceException(String code, Throwable t)
    {
        super(code, t);
    }

    public PersistenceException(Throwable t)
    {
        super(DEFAULT_CODE, t);
    }
}
