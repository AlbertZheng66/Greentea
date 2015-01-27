package com.xt.gt.jt.proc.result;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.utils.StringUtils;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.views.taglib.html.FormTag;

public class CollectionResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException {
		String attrName = StringUtils.capitalize(processParams.getBizHandler()) + "List";
		
		req.setAttribute(attrName, ret);
		ReservePrev reservePrev = new ReservePrev(req);
		reservePrev.save(req, attrName, ret);
		
		//只有输入参数为一个时，将输入参数放在FormBean中。
		//一般情况下，用户查询返回的情况
		if (params != null && params.length == 1) {
		    req.setAttribute(FormTag.BEAN_NAME, params[0]);
			reservePrev.save(req, FormTag.BEAN_NAME, params[0]);
		}
	}


	public boolean willProcess(Object ret, HttpServletRequest req) {
		if (ret == null) {
			return false;
		}
		return (ret instanceof Collection || ret.getClass().isArray());
	}

}
