package com.xt.gt.jt.proc.result;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.log.LogWriter;
import com.xt.gt.jt.bh.ISession;
import com.xt.gt.jt.proc.ActionParameter;

public class MapResultProcessor implements ResultProcessor {

	public MapResultProcessor() {

	}
	

	public boolean willProcess(Object ret, HttpServletRequest req) {
		if (ret == null) {
			return false;
		}
		return (ret instanceof Map);
	}

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException {
		Map m = (Map) ret;
		ReservePrev reservePrev = new ReservePrev(req);
		for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
			Object name = iter.next();
			if (!(name instanceof String)) {
				LogWriter.warn("返回的Map参数中，键值的类型不是字符串类型（String）！");
				return;
			}
			Object value = m.get(name);

			// 根据返回值的类型判断是存放在Session中还是Request中
			if (value instanceof ISession) {
				req.getSession().setAttribute((String) name, value);
			} else {
				req.setAttribute((String) name, value);
				reservePrev.save(req, (String)name, value);
			}
		}
	}


}
