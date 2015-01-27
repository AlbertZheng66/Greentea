package com.xt.gt.jt.proc.result;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.gt.jt.event.DefaultResponseEvent;
import com.xt.gt.jt.event.ResponseEvent;
import com.xt.gt.jt.proc.ActionParameter;

public class ResponseEventResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params)
			throws BaseException {
		ResponseEvent re = (DefaultResponseEvent) ret;

		ReservePrev reservePrev = new ReservePrev(req);
		if (re.getAttributeNames() != null) {
			for (Iterator iter = re.getAttributeNames().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				Object value = re.getValue(name);
				req.setAttribute(name, value);
				reservePrev.save(req, name, value);
			}
		}
		// 将值保存在Session中
		if (re.getSessionAttributeNames() != null) {
			for (Iterator iter = re.getSessionAttributeNames().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				req.getSession().setAttribute(name, re.getValue(name));
			}
		}
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		if (ret == null) {
			return false;
		}
		return (ret instanceof ResponseEvent);
	}

}
