package com.xt.core.val;

import com.xt.core.exception.SystemException;

/**
 * 校验器异常
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 当程序员定义的表达式类非法时抛出此异常。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-16
 */
public class ValidatorException extends SystemException {
    public ValidatorException (String msg) {
    	super(msg);
    }
}
