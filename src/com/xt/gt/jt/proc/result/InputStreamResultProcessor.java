package com.xt.gt.jt.proc.result;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.exception.ServiceException;
import com.xt.core.utils.IOHelper;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.gt.jt.screen.ScreenFlowManager;

/**
 * 对InputStream结果进行处理。
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
 * @date 2006-9-13
 */
public class InputStreamResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException {
		InputStream is = (InputStream) ret;
		try {
			res.setContentType("application/text");
			res.setHeader("Content-disposition", "attachment; filename=unknowed");
			IOHelper.i2o(is, res.getOutputStream());
		} catch (IOException e) {
			throw new ServiceException("输出文件异常！");
		}
		// 将屏幕流关闭，不再转换
		ScreenFlowManager.disable(req);
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		return (ret != null && ret instanceof InputStream);
	}

}
