package com.xt.gt.jt.screen;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BaseException;
import com.xt.core.utils.StringUtils;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 默认的屏幕流处理方式，应该作为责任链的最后一环。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 默认的页面转向规则. 规则为： 1. 如果方法以“goto”开头，则去掉“goto”， 然后加上BizHandler的名称
 * 2.如果方法以“add”开头，则将页面转向“List”页面，即“List” ＋ BizHandler的名称 3.如果方法不符合上述规则，则页面转向方法名称 +
 * BizHandler的名称
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
 * @date 2006-8-29
 */
public class RuleScreenFlow implements IScreenFlow {

	/**
	 * 采用默认规则进行处理的
	 */
	public String forword(HttpServletRequest req, ActionParameter pp, Object ret)
			throws BaseException {
		String prefix = null;
		String methodName = pp.getMethod();

		if (methodName.startsWith("goto")) {
			prefix = methodName.substring(4);
		} else if ("add".equals(methodName) || "update".equals(methodName)
				|| "delete".equals(methodName)) {
			prefix = "list";
		} else if ("edit".equals(methodName)) {
			prefix = "edit";
		} else {
			prefix = methodName;
		}

		String screen = StringUtils.capitalize(prefix)
				+ StringUtils.capitalize(pp.getBizHandler()) + ".jsp";

		if (pp.getAllPackages().trim().length() > 0) {
			screen = pp.getAllPackages() + "/" + screen;
		}
		// LogWriter.debug("defaultForword path", screen);

		return screen;
	}

}
