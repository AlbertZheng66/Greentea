package com.xt.views.taglib.html;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.IntegerUtils;
import com.xt.gt.sys.SystemConfiguration;

/**
 * 
 * <p>
 * Title: XT框架-GreenTea标签库
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
 * @date 2006-10-17
 */
public class PagerTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3586108374892218970L;

	/**
	 * 将查询结果的总数存放在Request属性中的名称
	 */
	public static final String TOTAL_ITEMS_IN_QUEST = "_total_items_";

	/**
	 * 每页的行数，可以通过配置文件指定，也可以通过用户输入
	 */
	private int rowsPerPage = SystemConfiguration.getInstance().readInt(
			"DEFAULT_ROWS_PER_PAGE", 50);

	private static Set<String> excludeParam = new HashSet<String>();

	static {
		excludeParam.add("_rows_per_page");
		excludeParam.add("_page_no");
		excludeParam.add("_null_");  //没有任何含义的参数
	}

	/**
	 * 记录总数
	 */
	private int totalCount = 0;

	public int doStartTag() throws JspException {
		// 如果属性参数为空，则抛出异常
		String totalItems = (String) pageContext.getRequest().getAttribute(
				TOTAL_ITEMS_IN_QUEST);
		
		if (totalItems == null) {
            LogWriter.warn("翻页标签未得到查询结果的总数!");
			return (SKIP_BODY);
		}
		totalCount = IntegerUtils.getInteger(totalItems);

		StringBuffer body = new StringBuffer();
		body.append("<table> <tr>");

		// 页数等于记录总数/每页的行数 + 1
		int totalPage = totalCount / rowsPerPage + 1;

		// 当前页的位置
		int pageNo = getCurrentPageNo();

		body.append("<td>");

		// 创建第一页
		createPageButton(body, 0, "first", pageNo == 0);

		body.append("</td><td> ");

		// 创建上一页
		createPageButton(body, pageNo - 1, "prev", pageNo == 0);

		body.append("</td><td> ");

		// 创建下一页
		createPageButton(body, pageNo + 1, "next", pageNo == (totalPage - 1));

		body.append("</td><td> ");

		// 创建最后一页
		createPageButton(body, totalPage - 1, "last", pageNo == (totalPage - 1));

		body.append("</td>");

		body.append("</tr></table> ");
		try {
			pageContext.getOut().write(body.toString());
		} catch (IOException e) {
            throw new JspException("IOException", e);
		}

		return (SKIP_BODY);

	}

	private void createPageButton(StringBuffer strBuf, int pageNo,
			String title, boolean none) {
		strBuf.append("<a href='");

		if (none) {
			strBuf.append("#");
		} else {
			// 提取此次HTTP请求的参数
			createOldUrl(strBuf);

			//
			strBuf.append("&_rows_per_page=").append(rowsPerPage);
			strBuf.append("&_page_no=").append(pageNo);
		}
		strBuf.append("'>").append(title).append("</a>");
	}

	private void createOldUrl(StringBuffer strBuf) {
		ServletRequest request = pageContext.getRequest();

		strBuf.append("?_null_=1");
		for (Iterator iter = request.getParameterMap().keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();

			// 排除指定的参数(与翻页相关的参数)
			if (excludeParam.contains(key)) {
				continue;
			}

			String[] data = (String[]) request.getParameterMap().get(key);
			for (int i = 0; i < data.length; i++) {
				try {
					strBuf.append("&").append(key).append("=").append(
							URLEncoder.encode(data[i], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// 发生编码异常不处理
					e.printStackTrace();
				}
			}
		}
		LogWriter.debug("url= " + strBuf.toString());
	}

	/**
	 * 返回当前页，如果请求中已经有翻页请求，则使用此翻页数，否则则认为是第一页
	 * 
	 * @return 返回当前页
	 */
	private int getCurrentPageNo() {
		ServletRequest request = pageContext.getRequest();
		int pageNo = 1;
		String pageNoStr = request.getParameter("_page_no");
		if (StringUtils.isNotBlank(pageNoStr)) {
			pageNo = IntegerUtils.getInteger(pageNoStr);
		}
		return pageNo;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
