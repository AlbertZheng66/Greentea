package com.xt.core.proc.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

public class JPAProcessorFactory implements ProcessorFactory {

	private EntityManagerFactory factory;

	public void onInit() {
		// test
		SystemConfiguration config = SystemConfiguration.getInstance();
		Database.getInstance().load(
				(DatabaseContext) config.readObject(
						SystemConstants.DATABASE_CONTEXT, null));
		DatabaseContext databaseContext = (DatabaseContext) config.readObject(
				SystemConstants.DATABASE_CONTEXT, null);
		Map<String, String> properties = new HashMap<String, String>();
		if (DatabaseContext.DATA_SOURCE.equals(databaseContext.getType())) {
			properties.put("javax.persistence.nonJtaDataSource",
					databaseContext.getDatabaseName());
		} else {
			properties.put("toplink.jdbc.driver", databaseContext
					.getDriverClass());
			properties.put("toplink.jdbc.url", databaseContext.getUrl());
			properties.put("toplink.jdbc.user", databaseContext.getUserName());
			properties.put("toplink.jdbc.password", databaseContext
					.getPassword());
		}
		factory = Persistence.createEntityManagerFactory("emName", properties);
	}

	public synchronized Processor createProcessor(Class serviceClass) {
		if (JPAAware.class.isAssignableFrom(serviceClass)) {
			EntityManager em = factory.createEntityManager();
			JPAProcessor processor = new JPAProcessor(em);
			return processor;
		} else {
			return null;
		}
	}

	public void onDestroy() {
		factory.close();
	}

}