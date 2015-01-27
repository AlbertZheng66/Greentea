package com.xt.core.proc.impl;

import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import java.sql.Connection;

public class IPOPersistenceProcessorFactory implements ProcessorFactory {

    private DatabaseContext databaseContext;

    public IPOPersistenceProcessorFactory() {
    }

    public void onInit() {
        // test
        SystemConfiguration config = SystemConfiguration.getInstance();
        Database.getInstance().load(
                (DatabaseContext) config.readObject(
                SystemConstants.DATABASE_CONTEXT, null));
        databaseContext = (DatabaseContext) config.readObject(
                SystemConstants.DATABASE_CONTEXT, null);
    }

    public synchronized Processor createProcessor(Class serviceClass) {
        if (IPOPersistenceAware.class.isAssignableFrom(serviceClass)) {
            IPOPersistenceManager persistenceManager = new IPOPersistenceManager();
            persistenceManager.setDatabaseContext(databaseContext);
            IPOPersistenceProcessor processor = new IPOPersistenceProcessor(
                    persistenceManager);
            return processor;
        }
        return null;
    }

    public void onDestroy() {
        // 关闭HSQL数据库。
        // jdbc:hsqldb:file:db/file;shutdown=true
        if (databaseContext != null
                && "org.hsqldb.jdbcDriver".equals(databaseContext.getDriverClass())) {
            databaseContext.setUrl(databaseContext.getUrl() + ";shutdown=true");
            Connection conn = ConnectionFactory.getConnection(databaseContext);
            ConnectionFactory.closeConnection(conn);
        }
        databaseContext = null;
    }
}
