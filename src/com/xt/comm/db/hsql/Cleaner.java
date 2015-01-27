package com.xt.comm.db.hsql;

import com.xt.core.app.init.SystemLifecycle;
import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.conn.DatabaseContextManager;
import com.xt.core.log.LogWriter;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.hsqldb.DatabaseManager;

/**
 * 负责将 HSQL 关闭.
 * @author albert
 */
public class Cleaner implements SystemLifecycle {

    /**
     * 日志实例
     */
    private final Logger logger = Logger.getLogger(Cleaner.class);

    public Cleaner() {
    }

    public void onInit() {
        // nothing to do
    }

    public void onDestroy() {
        Collection<DatabaseContext> databases = DatabaseContextManager.getInstance().getDatabaseContexts();
        for (Iterator<DatabaseContext> it = databases.iterator(); it.hasNext();) {
            DatabaseContext databaseContext = it.next();
            if (databaseContext.getDriverClass() != null
                    && databaseContext.getDriverClass().startsWith("org.hsqldb")) {
                // "jdbc:hsqldb:file:db/devDB;shutdown=true";
                databaseContext.setUrl(databaseContext.getUrl() + ";shutdown=true");
                LogWriter.info2(logger, "正在关闭数据库[%s]。", databaseContext);
                try {
                    Connection conn = ConnectionFactory.getConnection(databaseContext);
                    conn.commit();
                    DatabaseManager.getTimer().shutDown();
                } catch (Throwable t) {
                    logger.warn(String.format("关闭数据库[%s]时出现异常。", databaseContext), t);
                }
            }
        }
    }
}
