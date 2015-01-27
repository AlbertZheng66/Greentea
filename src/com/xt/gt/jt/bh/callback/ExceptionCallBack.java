package com.xt.gt.jt.bh.callback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 异常回调函数。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 当系统抛出异常时，可以通过注册异常回调函数进行处理。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-11-25
 */
public interface ExceptionCallBack {
	
	public void dealWith(HttpServletRequest req,
			HttpServletResponse res, Throwable t);
}
