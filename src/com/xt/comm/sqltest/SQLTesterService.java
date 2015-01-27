package com.xt.comm.sqltest;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.db.pm.Item;
import com.xt.core.proc.impl.IPOPersistenceAware;
import com.xt.core.service.LocalMethod;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.ui.fsp.Pagination;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

/**
 * 用于测试 SQL 语句是否正确，已经执行时间。注意：SQL执行结束后事务将回滚，因此不会影响数据的完整性。
 * @author Albert
 */
public class SQLTesterService implements IPOPersistenceAware {

    private transient IPOPersistenceManager persistenceManager;
    /**
     * 是否启用此功能。
     */
    private final static boolean enabled = SystemConfiguration.getInstance().readBoolean("sqlTester.enabled", false);

    public SQLTesterService() {
    }
    
    public long execute(String sql) {
        return execute(sql, true);
    }

    public long execute(String sql, final boolean isPagination) {
        if (!enabled) {
            throw new SQLTesterException("此功能已经被关闭。");
        }
        if (StringUtils.isEmpty(sql)) {
            throw new SQLTesterException("被测试的 SQL 语句不能为空。");
        }
        try {
            long startTime = System.currentTimeMillis();
            // 分页时设定分页参数
            if (isPagination) {
                persistenceManager.setStartIndex(0);
                persistenceManager.setFetchSize(Pagination.DEFAULT_ROWS_PER_PAGE);
            }
            if (isUpdate(sql)) {
                persistenceManager.execute(sql, null);
            } else {
                persistenceManager.query(sql, null, new Item() {

                    public Object createObject(ResultSet rs) throws SQLException {
                        return new Object();
                    }

                    public boolean isPagination() {
                        return isPagination;
                    }
                });
            }
            // 执行结束的时间
            long duration = System.currentTimeMillis() - startTime;
            return duration;
        } finally {
            persistenceManager.rollback();
        }
    }

    private boolean isUpdate(String sql) {
        String upperSql = sql.toUpperCase().trim();
        return (upperSql.startsWith("INSERT") || upperSql.startsWith("UPDATE") || upperSql.startsWith("DELETE"));
    }

    @LocalMethod
    public void setPersistenceManager(IPOPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @LocalMethod
    public IPOPersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }
}
