package com.xt.core.proc.impl;

import javax.servlet.http.HttpSession;

import com.xt.core.service.LocalMethod;

public interface HttpSessionAware {

	/**
	 * (由框架)设置一个HttpSession实例
	 * @param session
	 */
	@LocalMethod
	public void setHttpSession(HttpSession session);
	
	/**
	 * 返回一个HttpSession实例
	 * @param session
	 */
	@LocalMethod
	public HttpSession getHttpSession();

}
