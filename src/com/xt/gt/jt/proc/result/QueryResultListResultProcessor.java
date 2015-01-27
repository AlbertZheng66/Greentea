package com.xt.gt.jt.proc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.db.po.QueryResultList;
import com.xt.core.exception.BaseException;
import com.xt.core.utils.StringUtils;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.views.taglib.html.FormTag;
import com.xt.views.taglib.html.PagerTag;

/**
 * 查询结果列表处理器。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 当返回结果是查询列表时，使用此处理结果进行处理。
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
 * @date 2006-10-21
 */
public class QueryResultListResultProcessor implements ResultProcessor {

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params)
			throws BaseException {

		QueryResultList queryResultList = (QueryResultList) ret;

		// 返回错误页时，保存前一页的信息
		ReservePrev reservePrev = new ReservePrev(req);
	
		req.setAttribute(PagerTag.TOTAL_ITEMS_IN_QUEST, queryResultList
				.getTotalItems());
		reservePrev.save(req, PagerTag.TOTAL_ITEMS_IN_QUEST, queryResultList
				.getTotalItems());

		//如果是一个命名查询，则采用命名的名称，否则使用
		String attrName = queryResultList.getName();
		if (attrName == null) {
			attrName = StringUtils.capitalize(processParams.getBizHandler())
					+ "List";
		}

		req.setAttribute(attrName, queryResultList.getResultList());

		reservePrev.save(req, attrName, queryResultList.getResultList());

		// 只有输入参数为一个时，将输入参数放在FormBean中。
		// 一般情况下，用户查询返回的情况
		if (params != null && params.length == 1) {
			req.setAttribute(FormTag.BEAN_NAME, params[0]);
			reservePrev.save(req, FormTag.BEAN_NAME, params[0]);
		}
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		return (ret != null && ret instanceof QueryResultList);
	}

}
