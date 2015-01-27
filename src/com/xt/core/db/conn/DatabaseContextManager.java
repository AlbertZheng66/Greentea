package com.xt.core.db.conn;

import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class DatabaseContextManager {

    private static final DatabaseContextManager instance = new DatabaseContextManager();
    /**
     * 存储的上下文，主键是上下文的id，对应的值是DatabaseContext的实例
     */
    private Map<String, DatabaseContext> contexts = Collections.synchronizedMap(new HashMap<String, DatabaseContext>());
    /**
     * 日志实例
     */
    private final Logger logger = Logger.getLogger(DatabaseContextManager.class);
    /**
     * 默认数据库名
     */
    private String defaultId;

    private DatabaseContextManager() {
    }

    /**
     * 返回 JVM 中唯一的实例。
     * @return
     */
    static public DatabaseContextManager getInstance() {
        return instance;
    }

    /**
     * 根据名称返回指定的DatabaseContext实例。
     * @param id 数据环境编码
     * @return DatabaseContext实例
     * @throws SystemException
     */
    synchronized public DatabaseContext getDatabaseContext(String id)
            throws SystemException {
        if (StringUtils.isEmpty(id)) {
            throw new BadParameterException("数据库名称不能为空。");
        }
        if (!contexts.containsKey(id)) {
            throw new BadParameterException(String.format("数据库[%s]未定义。", id));
        }
        return ((DatabaseContext) contexts.get(id));
    }

    /**
     * 判断指定的数据源定义是否存在。
     * @param name
     * @return
     */
    public boolean exists(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return contexts.containsKey(name);
    }

    /**
     * 返回所有的数据源,以集合的形式包装DatabaseContext对象.
     *
     * @return Collection
     */
    public Collection<DatabaseContext> getDatabaseContexts() {
        return Collections.unmodifiableCollection(contexts.values());
    }

    /**
     * 返回所有的默认数据源.
     *
     * @return DatabaseContext
     */
    public DatabaseContext getDefaultDatabaseContext() throws SystemException {
        if (StringUtils.isEmpty(defaultId)) {
            throw new BadParameterException("尚未定义默认数据库。");
        }
        return getDatabaseContext(defaultId);
    }

    synchronized public void add(DatabaseContext context) {
        if (context == null) {
            return;
        }
        LogWriter.debug(logger, "add context.getId()=", context.getId());
        if (StringUtils.isEmpty(context.getId())) {
            throw new BadParameterException("数据库名称不能为空。");
        }
        if (context != null && context.getId() != null) {
            contexts.put(context.getId(), context);
        }
    }

    public void setDefaultId(String _defaultId) {
        defaultId = _defaultId;
    }

    public String getDefaultId() {
        return defaultId;
    }
}
