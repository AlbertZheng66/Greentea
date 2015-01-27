
package com.xt.core.proc.impl;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.impl.MasterSlavesContext;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 当采用一主多备的数据库设计时, 需要通过此工厂来实现使用主数据库还是备份数据库.
 * @author albert
 */
public class MultiIPOPersistenceFactory  implements ProcessorFactory {

    private final Logger logger = Logger.getLogger(MultiIPOPersistenceFactory.class);

    /**
     * 主数据库
     */
    private List<DatabaseContext> masterDatabaseContexts;

    /**
     * 多个备份数据库地址
     */
    private List<DatabaseContext> backupDatabaseContexts;

    public void onInit() {
         // test
        SystemConfiguration config = SystemConfiguration.getInstance();
        MasterSlavesContext msContext = (MasterSlavesContext)config.readObject(MasterSlavesContext.MASTERS_SLAVES_PARAM_NAME, null);
        if (msContext == null) {
            throw new SystemException("未定义主从库的相关信息。");
        }
        
        masterDatabaseContexts = msContext.getMasters();
        if (masterDatabaseContexts == null
                || masterDatabaseContexts.isEmpty()) {
            throw new SystemException("主库的配置不能为空。");
        }
        backupDatabaseContexts = msContext.getSlaves();
        LogWriter.info2(logger, "系统加载了主库[%s]，从库[%s]。", masterDatabaseContexts, backupDatabaseContexts);
    }

    public Processor createProcessor(Class serviceClass) {
         if (IPOPersistenceAware.class.isAssignableFrom(serviceClass)) {
            IPOPersistenceManager persistenceManager = new IPOPersistenceManager();
            MultiIPOPersistenceProcessor processor = new MultiIPOPersistenceProcessor(persistenceManager,
                    masterDatabaseContexts, backupDatabaseContexts);
            return processor;
        }
        return null;
    }

    public void onDestroy() {
        masterDatabaseContexts = null;
        backupDatabaseContexts = null;
    }

}
