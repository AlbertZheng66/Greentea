package com.xt.gt.jt.bh.callback;

import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.jt.proc.ActionParameter;

public interface RequestProcessor {

	/**
	 * 事件处理方法。
	 * 
	 * @param req
	 *            Http请求
	 * @param res
	 *            Http响应
	 * @param requestEvent
	 *            转换后的请求事件
	 * @param conn
	 *            数据库连接
	 * @return 处理后的返回结果
	 * @throws BaseException
	 */
	public abstract Object process(RequestEvent requestEvent,
			HttpServletResponse res, ActionParameter pp) throws BaseException;

}