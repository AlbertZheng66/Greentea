package com.xt.core.proc.impl;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.log.LogWriter;
import com.xt.core.session.Session;
import com.xt.core.utils.CollectionUtils;
import com.xt.proxy.Context;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class MultiIPOPersistenceProcessor extends IPOPersistenceProcessor {

    private final Logger logger = Logger.getLogger(MultiIPOPersistenceProcessor.class);

    /**
     * 主数据库(多个)
     */
    private final List<DatabaseContext> masterDatabaseContexts;
    
    private Random random = new Random(System.currentTimeMillis());

    /**
     * 多个备份数据库地址
     */
    private final List<DatabaseContext> backupDatabaseContexts;

    public MultiIPOPersistenceProcessor(IPOPersistenceManager persistenceManager,
            List<DatabaseContext> masterDatabaseContexts, List<DatabaseContext> backupDatabaseContexts) {
        super(persistenceManager);
        this.masterDatabaseContexts = masterDatabaseContexts;
        this.backupDatabaseContexts = backupDatabaseContexts;
    }

    @Override
    public void onCreate(Class serviceClass, Session session, Context context) {
        // nothing to do
    }

    /**
     * 在此方法处决定采用的主数据库还是从数据库
     * @param service
     * @param method
     * @param params
     * @return
     */
    @Override
    public Object[] onBefore(Object service, Method method, Object[] params) {
        DatabaseContext databaseContext = null;
        if (isReadonly(service, method)
                && CollectionUtils.isNotEmpty(backupDatabaseContexts)) {
            // 随机选择一个备份数据库
            // FIXME: 需要处理得更健壮，比如当备份数据库减少的时候，会出问题，
            int index = random.nextInt(backupDatabaseContexts.size());
            databaseContext = backupDatabaseContexts.get(index);
        } else {
            // 使用主库
            if (masterDatabaseContexts.size() == 1) {
                databaseContext = masterDatabaseContexts.get(0);
            } else {
                int index = random.nextInt(masterDatabaseContexts.size());
                databaseContext = masterDatabaseContexts.get(index);
            }
        }

        LogWriter.info2(logger, "类[%s]的方法[%s]使用了数据库资源[%s]。", service.getClass(),
                method.getName(), databaseContext);
        persistenceManager.setDatabaseContext(databaseContext);
        persistenceManager.setAutoCommit(false);

        return super.onBefore(service, method, params);
    }

    /**
     * 判断一个方法是否为只读，目前的实现是通过判断方法上是否有标注进行判断。
     * @param service 业务类实例
     * @param method  业务方法实例
     * @return 此方法是否只读。
     */
    protected boolean isReadonly(Object service, Method method) {
        if (service.getClass().getAnnotation(Readonly.class) != null) {
            return true;
        }
        if (method.getAnnotation(Readonly.class) != null) {
            return true;
        }
        return false;
    }
}
