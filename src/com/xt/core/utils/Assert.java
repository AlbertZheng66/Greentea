package com.xt.core.utils;

import com.xt.core.exception.BLException;
import com.xt.core.exception.BaseException;

/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public class Assert {
	public Assert() {
	}

	/**
	 * 业务逻辑断言操作，如果表达式为假，抛出事务逻辑异常。
	 * 
	 * @param exp
	 *            boolean 永真表达式
	 * @param code
	 *            String 错误消息代码
	 * @throws BLException
	 */
	public static void businessAssert(boolean exp, String code)
			throws BLException {
		if (!exp) {
			throw new BLException(code);
		}
	}

	public static void businessAssert(boolean exp, BaseException t)
			throws BaseException {
		if (!exp) {
			throw t;
		}
	}

}
