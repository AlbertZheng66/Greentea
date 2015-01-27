package com.xt.gt.jt.proc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.gt.jt.proc.ActionParameter;

public interface ResultProcessor {
	
	/**
	 * 是否处理这种结果
	 * @param ret 业务处理的返回结果
	 * @param req HTTP请求对象
	 * @return 处理这种情况返回true
	 */
	public boolean willProcess(Object ret, HttpServletRequest req);

	/**
	 * 对结果进行处理。
	 * 
	 * @param ret
	 *            处理结果
	 * @param req
	 *            HTTP请求实例
	 * @param res
	 *            HTTP响应实例
	 * @param bizHandler
	 *            事务处理类
	 * @param pp
	 *            处理参数
	 * @throws BaseException
	 */
	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException;
}
