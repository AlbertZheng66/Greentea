package com.xt.gt.jt.bh.callback;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BLException;
import com.xt.core.exception.BaseException;
import com.xt.core.exception.POException;
import com.xt.core.exception.ServiceException;
import com.xt.core.exception.ValidateException;
import com.xt.core.log.LogWriter;
import com.xt.core.message.ErrorMessage;
import com.xt.gt.jt.proc.result.ReservePrev;
import com.xt.gt.jt.proc.result.ajax.AjaxUtils;
import com.xt.gt.jt.screen.IScreenFlow;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.views.taglib.html.MessagesTag;

/**
 * 缺省的异常回调处理部分。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-11-25
 */
public class DefaultExceptionCallBack implements ExceptionCallBack {
	
    /**
     * 当系统产生未处理的错误时将调整到此页面
     */
	public static final String ERROR_SCREEN = "errorScreen";

	public void dealWith(HttpServletRequest req, HttpServletResponse res,
			Throwable t) {
		LogWriter.error("系统异常", t);
		
		// 处理Ajax方式的请求
		if (AjaxUtils.isAjaxRequest(req)) {
			try {
				OutputStream os = res.getOutputStream();
				AjaxUtils.toAjax(os, res, t);
				os.flush();
			} catch (IOException e) {
				forwordErrorScreen(req, res, t);
			}
			return;
		}
		
		if (getValidateException(t) != null) {
			ValidateException ve = getValidateException(t);

			String message = null;
			if (ve != null) {
				message = ve.getCode();
			} else {
				message = ((BaseException) t).getUserMessage();
			}
			
			LogWriter.debug("GET_VALUE_FROM_PARAMS......................");
			
//			// 指示页面从请求参数中取值
//			req.setAttribute(BaseFieldTag.GET_VALUE_FROM_PARAMS, 
//					BaseFieldTag.GET_VALUE_FROM_PARAMS);

			// 出现校验错误，则返回到前一个页
			req.setAttribute(MessagesTag.ERROR_MESSAGE_IN_REQUEST, message);
			forwordPrevScreen(req, res);
		} else 	if (t instanceof ServiceException) {
			ServiceException se = getServiceException(t);

			String message = null;
			if (se != null) {
				message = se.getUserMessage();
			} else {
				message = ((BaseException) t).getUserMessage();
			}

			// 出现严重的错误，则返回到错误页
			req.setAttribute(MessagesTag.ERROR_MESSAGE_IN_REQUEST, message);
			forwordErrorScreen(req, res, t);
		} else {
			// 出现严重的错误，则返回到错误页
			ErrorMessage errorMessage = processError(t);
			LogWriter.error(errorMessage.getCode(), t);
			forwordErrorScreen(req, res, t);
		}
	}

	/**
	 * 处理异常。对异常进行处理
	 * 
	 * @param ex
	 *            异常
	 */
	private ErrorMessage processError(Throwable t) {
		ErrorMessage errorMessage = null;
		if (t instanceof POException) {
			POException poe = (POException) t;
			errorMessage = new ErrorMessage(poe.getUserMessage());
			LogWriter.error("数据库（持久对象）异常！", t);
			if (poe.getCause() != null) {
				LogWriter.error("原始异常:", t);
			}
		} else if (t instanceof BLException) {
			errorMessage = new ErrorMessage(((BLException) t).getUserMessage());
			LogWriter.error("事务逻辑异常！", t);
		} else if (t instanceof Exception) {
			errorMessage = new ErrorMessage("00004");
			LogWriter.error("系统未知异常！", t);
		} else if (t instanceof RuntimeException) {
			errorMessage = new ErrorMessage("00007");
			LogWriter.error("系统严重异常，系程序问题引发，请通知管理员！", t);
		} else {
			errorMessage = new ErrorMessage("00004");
			LogWriter.error("系统未知异常！", t);
		}

		return errorMessage;
	}

	/**
	 * 判断一个异常是否是因为ValidateException异常引起的。
	 * 
	 * @param e
	 * @return
	 */
	private ServiceException getServiceException(Throwable e) {
		if (e != null) {
			if (e instanceof ServiceException) {
				return (ServiceException) e;
			} else {
				return getServiceException(e.getCause());
			}
		}
		return null;
	}
	
	private ValidateException getValidateException(Throwable e) {
		if (e != null) {
			if (e instanceof ValidateException) {
				return (ValidateException) e;
			} else {
				return getValidateException(e.getCause());
			}
		}
		return null;
	}

	private void forwordPrevScreen(HttpServletRequest req,
			HttpServletResponse res) {
		LogWriter.debug("forwordPrevScreen...............");
		String screen = (String) req.getSession().getAttribute(
				IScreenFlow.SYS_PREV_SCREEN);

		ReservePrev.refund(req);

		if (screen == null) {
			screen = "/index.htm";
		}

		// 设置从请求参数中读取数据
		req.setAttribute("GET_VALUE_FROM_PARAMS", "Y");
		LogWriter.debug("SYS_PREV_SCREEN", screen);
		try {
			req.getRequestDispatcher(screen).forward(req, res);
		} catch (ServletException e) {
			LogWriter.error("转向前页时发生了Servlet异常", e);
			forwordErrorScreen(req, res, e);
		} catch (IOException e) {
			LogWriter.error("转向前页时发生了IO异常", e);
			forwordErrorScreen(req, res, e);
		}
	}

	private void forwordErrorScreen(HttpServletRequest req,
			HttpServletResponse res, Throwable t) {
		LogWriter.debug("forwordErrorScreen...............");
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		req.setAttribute("ERROR_STACK_TRACE", sw.toString());
		String screen = SystemConfiguration.getInstance().readString(
				ERROR_SCREEN, "/error.jsp");
		LogWriter.debug("ERROR_SCREEN", screen);
		try {
			req.getRequestDispatcher(screen).forward(req, res);
		} catch (ServletException e) {
			LogWriter.error("转向错误页时发生了Servlet异常", e);
		} catch (IOException e) {
			LogWriter.error("转向错误页时发生了IO异常", e);
		}
	}

}
