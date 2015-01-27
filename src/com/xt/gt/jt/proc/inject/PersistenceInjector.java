package com.xt.gt.jt.proc.inject;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.db.pm.PersistenceManager;
import com.xt.core.utils.BeanHelper;
import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 在Service实体中注入持久化类。
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
 * @date 2006-9-7
 */
public class PersistenceInjector implements RequestInjector {

	// 在系统配置文件中定义使用的PersistenceManager
	SystemConfiguration sc = SystemConfiguration.getInstance();

	public PersistenceInjector() {
	}

	public Object inject(Object bizHandler) {
		// 可以在配置文件中定义持久化处理器，如果未定义，则使用IPO方式进行处理。
		PersistenceManager pm = (PersistenceManager) sc.readObject(
				SystemConstants.PERSISTENCE_MANAGER,
				new IPOPersistenceManager());

		DatabaseContext dc = (DatabaseContext) sc.readObject(
				SystemConstants.DATABASE_CONTEXT, null);
		pm.setDatabaseContext(dc);

		// 要检查业务类是否定义了持久化类
		BeanHelper.copyProperty(bizHandler, "persistenceManager", pm);
		return pm;

	}

	public boolean needInject(BizHandlerInfo bizHandlerInfo,
			DealMethodInfo dmInfo) {
		return bizHandlerInfo.isNeedPersistence();
	}

}
