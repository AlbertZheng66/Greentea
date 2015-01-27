package com.xt.gt.jt.proc.result;

import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BadParameterException;
import com.xt.core.log.LogWriter;

public class ReservePrev {
	/**
	 * 在Session中保存前一次的处理结果，当有错误发生时，将这些结果返回给前一个界面。
	 * 
	 */
	public static final String PREV_PROCESS_RESULT_IN_SESSION = "PREV_PROCESS_RESULT_IN_SESSION";

	/**
	 * 当请求中有这个参数时，系统将不进行清除和保留参数处理
	 */
	public static final String NOT_RESERVE = "_NOT_RESERVE_";

	public ReservePrev(HttpServletRequest req) {
		if (req != null && req.getParameter(NOT_RESERVE) == null) {
			req.getSession().removeAttribute(PREV_PROCESS_RESULT_IN_SESSION);
		}
	}

	public void save(HttpServletRequest req, String name, Object value) {
		// 不保留参数
		if (req == null || req.getParameter(NOT_RESERVE) != null) {
			return;
		}

		// 有些对象（空，流）不保存
		if (value == null || value instanceof InputStream
				|| value instanceof Reader) {
			return;
		}

		// LogWriter.debug("reserve name", name);
		// LogWriter.debug("reserve value", value);
		if (req == null || name == null) {
			throw new BadParameterException("参数（HttpServletRequest）和参数名称不能为空");
		}
		SoftReference<Map<String, Object>> soft = (SoftReference<Map<String, Object>>) req.getSession()
				.getAttribute(PREV_PROCESS_RESULT_IN_SESSION);
		if (soft == null) {
			soft = new SoftReference<Map<String, Object>>(new HashMap<String, Object>());
			req.getSession().setAttribute(PREV_PROCESS_RESULT_IN_SESSION, soft);
		}
		Map<String, Object> map = soft.get();
		if (map != null) {
			map.put(name, value);
		}
	}

	/**
	 * 将保存的前一次的值重新放到请求属性中
	 * 
	 * @param req
	 */
	public static void refund(HttpServletRequest req) {
		SoftReference<Map<String, Object>> soft = (SoftReference<Map<String, Object>>) req.getSession()
				.getAttribute(PREV_PROCESS_RESULT_IN_SESSION);
		if (soft != null) {
			Map<String, Object> map = soft.get();
			if (map != null) {
				for (Iterator<Entry<String, Object>> iter = map.entrySet().iterator(); iter.hasNext();) {
					Entry<String, Object> entry = iter.next();
					String name = entry.getKey();
					Object value = entry.getValue();
					LogWriter.debug("refund name", name);
					LogWriter.debug("refund value", value);
					req.setAttribute(name, value);
				}
			}
		}
	}
}
