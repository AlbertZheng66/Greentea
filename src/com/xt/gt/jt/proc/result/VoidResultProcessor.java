package com.xt.gt.jt.proc.result;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.event.DefaultResponseEvent;
import com.xt.gt.jt.event.ResponseEvent;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 在方法没有参数返回的情况下,检查是否存在ResponseEvent属性，如果有，则返回之；
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
 * @date 2006-9-2
 */
public class VoidResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params)
			throws BaseException {
		Method gre = ClassHelper
				.getMethod(bizHandler, "getResponseEvent", null);
		ReservePrev reservePrev = new ReservePrev(req);
		if (null != gre
				&& ResponseEvent.class.isAssignableFrom(gre.getReturnType())) {
			ResponseEvent re = (DefaultResponseEvent) BeanHelper.getProperty(
					bizHandler, "responseEvent");
			LogWriter.debug("attr", re.getAttributeNames());
			for (Iterator iter = re.getAttributeNames().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				req.setAttribute(name, re.getValue(name));
				reservePrev.save(req, name, re.getValue(name));
			}
		}
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		return (null == ret);
	}

}
