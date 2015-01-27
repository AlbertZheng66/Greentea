package com.xt.gt.jt.proc;

import com.xt.core.db.pm.PersistenceManager;
import com.xt.core.utils.BooleanUtils;
import com.xt.gt.jt.event.RequestEvent;

public class DefaultSortProcessor implements SortProcessor {
	/**
	 * 处理数据库排序功能。系统将读取HTTP请求中的排序参数，和排序方式，然后赋给 持久化管理器，由其负责排序处理。
	 * 
	 * @param persistenceManager
	 * @param req
	 */
	public void processSort(PersistenceManager persistenceManager,
			RequestEvent req) {

		// 首先读取HTTP请求的排序参数,即排序字段
		String sortColumn = req.getParameter("_SORT_COLUMN_");

		if (null != sortColumn && persistenceManager != null) {
			// 是否是升序
			boolean asc = BooleanUtils.isTrue(req.getParameter("_ASC_SORT_"),
					true);
            //FIXME: 这样处理排序有问题
			// persistenceManager.sort(sortColumn, asc);

		}
	}
}

