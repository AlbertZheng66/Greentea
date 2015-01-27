package com.xt.gt.jt.proc;

import com.xt.core.db.pm.PersistenceManager;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.RegExpUtils;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.sys.SystemConfiguration;

public class DefaultPaginationProcessor implements PaginationProcessor {

	/**
	 * 记录起始位置（翻页属性）的参数名称
	 */
	private static String pageNoParam;

	/**
	 * 每页记录数（翻页属性）的参数名称
	 */
	private static String rowsPerPageParam;

	// 是否采用自动排序方式
	private static boolean autoSort = true;

	static SystemConfiguration sc;

	static {
		sc = SystemConfiguration.getInstance();

		//
		pageNoParam = sc.readString("PAGE_NO", "_page_no");

		rowsPerPageParam = sc.readString("ROWS_PER_PAGE", "_rows_per_page");

		// 是否自动排序
		autoSort = BooleanUtils.isTrue(sc.readString("AUTO_SORT"), true);
	}

	/**
	 * 处理翻页参数
	 * 
	 * @param persistenceManager
	 * @param req
	 */
	public void processTurnPage(PersistenceManager persistenceManager,
			RequestEvent req) {

		// 首先读取翻页开关以表示这次请求是否翻页
		boolean turnPageOn = BooleanUtils.isTrue(req
				.getParameter("_TURN_PAGE_ON_"), false);

		if (turnPageOn && persistenceManager != null) {
			// 注入开始位置
			String pn = req.getParameter(pageNoParam);
			int pageNo = 1; // 默认为1
			if (pn != null && RegExpUtils.isInteger(pn)) {
				pageNo = Integer.parseInt(pn);
			}
			LogWriter.debug("pageNo", pageNo);
			persistenceManager.setStartIndex(pageNo);

			// 注入开始位置
			String rp = req.getParameter(rowsPerPageParam);
			int rowsPerPage = sc.readInt("DEFAULT_ROWS_PER_PAGE", 50); // 默认为50行
			if (rp != null && RegExpUtils.isInteger(rp)) {
				rowsPerPage = Integer.parseInt(rp);
			}
			LogWriter.debug("rowPerPage", rowsPerPage);
			persistenceManager.setFetchSize(rowsPerPage);
		}  

		// 处理多个列表同时翻页的情况？？？
		if (turnPageOn && persistenceManager != null) {
			// 注入开始位置
			String[] pageNames = req.getParameterValues(pageNoParam);
			
			if (pageNames == null || pageNames.length == 0) {
				return;
			}
//			Pagenation[] pagenations = new Pagenation[pageNames.length];
//			
//			for (int i = 0; i < pageNames.length; ++i) {
//				String pageName = pageNames[i];			
//				Pagenation pagenation = new Pagenation();
//				pagenation.setName(pageName);
//				
//				// 设置页面号
//				int pageNo = 1; // 默认为1
//				if (pageName != null && RegExpUtils.isInteger(pageName)) {
//					pageNo = Integer.parseInt(pageName);
//				}
//				pagenation.setPageNo(pageNo);
//
//				// 注入开始位置
//				String rp = req.getParameter(pageName + rowsPerPageParam);
//				int rowsPerPage = sc.readInt("DEFAULT_ROWS_PER_PAGE", 50); // 默认为50行
//				if (rp != null && RegExpUtils.isInteger(rp)) {
//					rowsPerPage = Integer.parseInt(rp);
//				}
//				pagenation.setRowsPerPage(rowsPerPage);
//				
//			}
//			persistenceManager.setPagenation(pagenations);
		}  
	}
}
