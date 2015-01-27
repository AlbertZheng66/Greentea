package com.xt.gt.jt.proc;

import com.xt.core.db.pm.PersistenceManager;
import com.xt.gt.jt.event.RequestEvent;

public interface SortProcessor {
	public void processSort(PersistenceManager persistenceManager,
			RequestEvent req);
}
