package com.xt.gt.jt.proc.result.ajax;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.event.ResponseEvent;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.gt.jt.proc.result.ResultProcessor;

public class AjaxResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params)
			throws BaseException {

		if (ret == null) {
			Method gre = ClassHelper.getMethod(bizHandler, "getResponseEvent",
					null);

			if (null != gre
					&& ResponseEvent.class.isAssignableFrom(gre.getReturnType())) {
				ret = BeanHelper.getProperty(bizHandler, "responseEvent");

			}
		}
		try {
			res.setContentType("application/xml");
			AjaxUtils.toAjax(res.getOutputStream(), res, ret);
		} catch (IOException e) {
			throw new BaseException("处理Ajax结果时异常", e);
		}
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		return (AjaxUtils.isAjaxRequest(req));
	}

}
