package com.xt.gt.jt.proc.result;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.Pair;
import com.xt.gt.jt.bh.ISession;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.views.taglib.html.FormTag;

public class ObjectResultProcessor implements ResultProcessor {

	/**
	 * 返回参数中不能处理的类型
	 */
	private Set<Class> excludeTypes = new HashSet<Class>();

	public ObjectResultProcessor() {
		// 以下为返回参数中不能处理的类型
		excludeTypes.add(Integer.class);
		excludeTypes.add(Double.class);
		excludeTypes.add(Float.class);
		excludeTypes.add(Long.class);
		excludeTypes.add(Character.class);
		excludeTypes.add(Byte.class);
		excludeTypes.add(String.class);
		excludeTypes.add(int.class);
		excludeTypes.add(double.class);
		excludeTypes.add(long.class);
		excludeTypes.add(float.class);
		excludeTypes.add(char.class);
		excludeTypes.add(byte.class);
	}

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException {

		// 首先将这个返回结果存放到request属性中，供Struts的Form提取。
		req.setAttribute(FormTag.BEAN_NAME, ret);
		ReservePrev reservePrev = new ReservePrev(req);
		reservePrev.save(req, FormTag.BEAN_NAME, ret);

		Pair[] pairs = BeanHelper.getProperties(ret);
		if (null != ret) {
			for (int i = 0; i < pairs.length; i++) {
				Pair p = pairs[i];
				Object value = p.getValue();
				// 根据返回值的类型判断是存放在Session中还是Request中
				if (value instanceof ISession) {
					req.getSession().setAttribute(p.getName(), value);
				} else {
					reservePrev.save(req, p.getName(), value);
					req.setAttribute(p.getName(), value);
				}
			}
		}
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		if (ret == null) {
			return false;
		}
		return (!ret.getClass().isEnum() && !excludeTypes.contains(ret
				.getClass()));
	}

}
