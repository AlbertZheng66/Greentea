package com.xt.core.db.conn;

import com.xt.core.service.IService;

/**
 * @deprecated 
 * @author albert
 */
public class RemoteDatatbaseService implements IService{

	public RemoteDatatbaseService() {
	}
	
	public DatabaseContext getDatabaseContext() {
        DatabaseContextManager manager = DatabaseContextManager.getInstance();
		return manager.getDefaultDatabaseContext();
	}

	
	
}
