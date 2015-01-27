package com.xt.core.proc.impl;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.service.LocalMethod;

public interface IPOPersistenceAware {
	
	@LocalMethod
	public void setPersistenceManager(IPOPersistenceManager persistenceManager);

    @LocalMethod
	public IPOPersistenceManager getPersistenceManager();
}
