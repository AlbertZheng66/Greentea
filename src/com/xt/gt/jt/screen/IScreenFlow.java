package com.xt.gt.jt.screen;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BaseException;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 屏幕流接口。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 如果框架需要实现自定义的屏幕流处理方式，需要实现此接口，
 * 并在ScreenFlowManager中注册，ScreenFlowManager将按照注册的顺序逐一
 * 调用（采用任务链模式）。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-11
 */
public interface IScreenFlow {
	
	/**
	 * 存放在Session中的前一个页面的属性名称
	 */
	public final static String SYS_PREV_SCREEN = "SYS_PREV_SCREEN";

	/**
	 * 将JSP页面跳转到哪个页面.
	 * @param req Http请求，传入时不为空。
	 * @param pp  处理参数，传入时不为空。
	 * @return JSP页面的路径，如果不能处理，则返回空。
	 * @throws BaseException
	 */
	public String forword(HttpServletRequest req,
			ActionParameter pp, Object ret) throws BaseException;

}