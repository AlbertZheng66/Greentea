package com.xt.gt.jt.proc;

import com.xt.core.db.pm.PersistenceManager;
import com.xt.gt.jt.event.RequestEvent;

public interface PaginationProcessor {
	public void processTurnPage(PersistenceManager persistenceManager,
			RequestEvent req);
}
