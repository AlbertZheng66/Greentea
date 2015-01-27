package com.xt.gt.jt.event;

import com.xt.core.exception.SystemException;

/**
 * 请求事件异常。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 当解析请求事件时产生的异常。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-24
 */
public class RequestEventException extends SystemException {
	public RequestEventException(String code, Throwable t) {
		super(code, t);
	}
}
