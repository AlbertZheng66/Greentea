package com.xt.core.db.pm;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.po.IPO;
import com.xt.core.db.po.IPOBuilder;
import com.xt.core.db.po.IPOFactory;
import com.xt.core.db.po.QueryResultList;
import com.xt.core.db.po.QueryUtils;
import com.xt.core.db.pm.impl.PostgreDialect;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.POException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.map.DBMapping;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.Pair;
import com.xt.core.utils.PreparedStatementHelper;
import com.xt.core.db.po.mapping.DefaultDBMapping;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 使用IPO方式进行持久化操作
 * <p>
 * Title: XT框架-持久化逻辑部分
 * </p>
 * <p>
 * Description: 针对实现IPO接口的对象,进行持久化操作.这个管理器针对IPOBuilder
 * 进行了简单的封装实现了PersistenceManager方式.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-25
 */
public class IPOPersistenceManager implements PersistenceManager {

    private static PersistenceDialect persistenceDialect;
    private final Logger logger = Logger.getLogger(IPOPersistenceManager.class);
    /**
     * 数据库连接
     */
    private Connection conn;
    private int startIndex = 0; // 默认的页数为0，即第一页
    private int fetchSize = SystemConfiguration.getInstance().readInt(SystemConstants.PAGINATION_PAGE_SIZE, 200); // 默认的行数

    // 通过配置文件得到此类
    private DBMapping mapping;

//    // 排序字段
//    private String sortColumn;
//
//    // 是否是升序
//    private boolean ascSort;

    public IPOPersistenceManager() {
        // 通过配置文件得到此类
        mapping = (DBMapping) SystemConfiguration.getInstance().readObject(
                SystemConstants.DATABASE_MAPPING, new DefaultDBMapping());
    }


    static {
        persistenceDialect = (PersistenceDialect) SystemConfiguration.getInstance().readObject(SystemConstants.DATABASE_DIALECT,
                new PostgreDialect());
    }

    /**
     * 插入操作
     *
     * @param newObject
     *            IPersistence 插入的实例（包含数据的JavaBean）
     * @throws PersistenceException
     *             持久化异常
     * @return boolean
     */
    public boolean insert(IPersistence newObject) throws PersistenceException {
        boolean success = false;
        IPO ipo = getIPO(newObject);
        try {
            IPOBuilder builder = new IPOBuilder(ipo);
            success = builder.insert(conn);
        } catch (POException ex) {
            throw new PersistenceException(ex);
        }
        return success;
    }

    /**
     * 更新操作（主键未发生变化时）
     *
     * @param newObject
     *            IPersistence 更新后的实例（包含数据的JavaBean）
     * @throws PersistenceException
     *             持久化异常
     * @return boolean
     */
    public boolean update(IPersistence obj) throws PersistenceException {
        boolean success = false;
        IPO ipo = getIPO(obj);
        try {
            IPOBuilder builder = new IPOBuilder(ipo);
            success = builder.update(conn);

        } catch (POException ex) {
            throw new PersistenceException(ex);
        }

        return success;
    }

    /**
     * 更新操作(主键发生了变化)
     *
     * @param obj
     *            IPersistence 更新前的实例（包含数据的JavaBean）
     * @throws PersistenceException
     *             持久化异常
     * @return boolean
     */
    public boolean update(IPersistence obj, Serializable pk)
            throws PersistenceException {
        boolean success = false;

        // 要检查一下对象是否存在，
        Object old = findByPK(obj.getClass(), pk);
        if (old == null) {
            throw new PersistenceException("要更新的对象已经被删除!");
        }
        try {
            // 将新对象的值付给原有对象
            BeanHelper.copy(old, obj);
            IPO ipo = getIPO(old);
            IPOBuilder builder = new IPOBuilder(ipo);
            success = builder.update(conn);
        } catch (POException ex) {
            throw new PersistenceException(ex);
        }

        return success;
    }

    /**
     * 删除操作
     *
     * @param object
     *            IPersistence 插入的实例（包含数据的JavaBean）
     * @throws PersistenceException
     *             持久化异常
     * @return boolean
     */
    public boolean delete(IPersistence object) throws PersistenceException {
        boolean success = false;
        IPO ipo = getIPO(object);
        try {
            IPOBuilder builder = new IPOBuilder(ipo);
            success = builder.delete(conn);
        } catch (POException ex) {
            throw new PersistenceException(ex);
        }

        return success;
    }

    /**
     * 通过主键查找
     *
     * @param object
     *            IPersistence 插入的实例（包含数据的JavaBean）
     * @throws PersistenceException
     *             持久化异常
     * @return boolean
     */
    public Object findByPK(IPersistence object) throws PersistenceException {
        IPO ipo = getIPO(object);
        try {
            IPOBuilder builder = new IPOBuilder(ipo);
            return builder.findByPK(conn);
        } catch (POException ex) {
            throw new PersistenceException(ex);
        }
    }

    public <T> T findByPK(Class<T> clazz, Serializable pk)
            throws PersistenceException {
        LogWriter.info(logger, String.format("根据主键[%s]查找类[%s]的实例。", pk, clazz));
        if (clazz == null) {
            throw new BadParameterException("类参数不能为空。");
        }

        if (pk == null) {
            throw new BadParameterException("主键不能为空。");
        }

        // 校验类是否正常
        Class newClass = toIPOClass(clazz);

        IPO ipo = (IPO) ClassHelper.newInstance(newClass);
        String[] pks = ipo.__getPrimaryKeys();
        if (pks == null || pks.length != 1) {
            throw new BadParameterException("主键不能为空，且只能为单主键。");
        }
        String propertyName = findPropertyName(clazz, pks[0]);
        if (StringUtils.isEmpty(propertyName)) {
            throw new BadParameterException(String.format("为找到主键[%s]对应的属性。", pks[0]));
        }
        BeanHelper.copyProperty(ipo, propertyName, pk);
        return (T)findByPK(ipo);
    }

    /**
     * 根据一个类及字段名称，找到其对应的属性名称。
     * @return
     */
    private String findPropertyName(Class clazz, String columnName) {
        if (columnName == null) {
            return null;
        }
        String[] pns = mapping.getPropertyNames(clazz);
        for (int i = 0; i < pns.length; i++) {
            String propertyName = pns[i];
            if (columnName.equals(mapping.getColumnName(clazz, propertyName))) {
                return propertyName;
            }
        }
        return null;
    }

    public List findAll(IPersistence object, String sqlWhere, List params,
            String sqlOrder) throws PersistenceException {
        IPO ipo = getIPO(object);
        IPOBuilder builder = new IPOBuilder(ipo);
        ipo.setFetchSize(fetchSize);
        ipo.setStartIndex(startIndex);

        // 用户排序优先（不再使用这种方式排序，比较隐晦）
//        if (StringUtils.isBlank(sqlOrder) && StringUtils.isNotBlank(sortColumn)) {
//            sqlOrder = " ORDER BY " + sortColumn;
//            if (!ascSort) {
//                sqlOrder += " DESC ";
//            }
//        }

        QueryResultList list = builder.findAll(conn, false, sqlWhere, params,
                sqlOrder);

        return list.getResultList();
    }

    public <T>  T findFirst(Class<T> clazz, String sqlWhere, List params,
			String sqlOrder) throws PersistenceException {
        List<T> results = findAll(clazz, sqlWhere, params, sqlOrder);
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public List query(String sql, List params, final Item item)
            throws PersistenceException {
        // 检查输入
        if (null == sql || null == item) {
            throw new BadParameterException("SQL语句与处理方式都不能为空！");
        }

        int _startIndex = 0;
        int _fetchSize = Integer.MAX_VALUE;
        if (item.isPagination()) {
            _startIndex = this.startIndex;
            _fetchSize = this.fetchSize;
        }
        QueryResultList list = query(sql, params, item, _startIndex, _fetchSize);
        return list.getResultList();
    }

    public QueryResultList query(String sql, List params, final Item item,
            int startIndex, int rowsPerPage) throws PersistenceException {
//        if (StringUtils.isNotBlank(sortColumn)) {
//            sql += " ORDER BY " + sortColumn;
//            if (!ascSort) {
//                sql += " DESC ";
//            } else {
//                sql += " ASC ";
//            }
//        }

        QueryResultList list = QueryUtils.findBySQL(conn, sql, params, item,
                startIndex, rowsPerPage);
        return list;
    }

    public List query(Class clazz, String sql, List params)
            throws PersistenceException {
        // 校验参数
        if (clazz == null || sql == null) {
            throw new BadParameterException("类或者SQL语句都不能为空！");
        }

        // 构造处理结果方法
        DefaultItem item = new DefaultItem(clazz, mapping);
        return query(sql, params, item);
    }

    /**
     * 调用存储过程
     *
     * @return boolean
     */
    public boolean callProcedure(String name) throws PersistenceException {
        return true;
    }

    /**
     * 注意：方法不复制空值
     *
     * @param object
     * @return
     * @throws PersistenceException
     */
    private IPO getIPO(Object object) throws PersistenceException {
        if (object == null) {
            throw new PersistenceException("要进行持久化操作的对象为空", null);
        }
        if (!(object instanceof IPO)) {
            Object ipo = IPOFactory.newInstance().create(object.getClass());
            Pair[] pairs = BeanHelper.getProperties(object);
            for (int i = 0; i < pairs.length; i++) {
                Pair pair = pairs[i];
                // LogWriter.debug(logger, "name=" + pair.getName() + "; value=", pair.getValue() );
                if (pair.getValue() != null) {
                    BeanHelper.copyProperty(ipo, pair.getName(), pair.getValue());
                }
            }
            return (IPO) ipo;
        }
        return (IPO) object;
    }

    public Connection getConnection() {
        return conn;
    }

    /**
     * 需要外部来保证clazz是一个
     */
    public <T> List<T> findAll(Class<T> clazz, String sqlWhere, List params,
            String sqlOrder) throws PersistenceException {
        // 校验类是否正常
        Class newClass = toIPOClass(clazz);

        IPO ipo = (IPO) ClassHelper.newInstance(newClass);
        ipo.setFetchSize(fetchSize);
        ipo.setStartIndex(startIndex);
        IPOBuilder builder = new IPOBuilder(ipo);
        QueryResultList list = builder.findAll(conn, false, sqlWhere, params,
                sqlOrder);

        // 防止发生意外，即list为空的情况
        if (list == null) {
            return new ArrayList();
        }

        return list.getResultList();
    }
    
    public <T> List<T> findAll(Class<T> clazz) throws PersistenceException {
        return findAll(clazz, null, null, null);
    }

    /**
     * 校验一个类是否为空，是否实现了IPO接口，如果为空或者未实现IPO接口， 则抛出BadParameterException。
     *
     * @param clazz
     */
    private Class toIPOClass(Class clazz) {
        // 校验
        if (clazz == null) {
            throw new BadParameterException("参数中类不能未空！");
        }

        if (IPO.class.isAssignableFrom(clazz)) {
            return clazz;
        }

        return IPOFactory.newInstance().getClass(clazz);
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public List queryById(String sqlId, List params, Item item)
            throws PersistenceException {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql, List params) {
        LogWriter.debug("execute sql=", sql);
        LogWriter.debug("execute params=", params);
        if (sql == null) {
            throw new BadParameterException("SQL语句不能为空！");
        }
        try {
            PreparedStatement pStmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    PreparedStatementHelper.setValue(pStmt, i + 1, params.get(i));
                }
            }
            return pStmt.execute();
        } catch (SQLException e) {
            throw new PersistenceException("执行SQL语句时发生异常！sql=" + sql, e);
        }
    }

    public void setDatabaseContext(DatabaseContext dc) {
        conn = ConnectionFactory.getConnection(dc);
        if (null == conn) {
            throw new SystemException("数据库连接未定义(" + dc.toString() + ")");
        }
        try {
            // 全部手工提交
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new SystemException("数据库连接设置参数异常", e);
        }
    }

//	public List like(Object obj, boolean lenient, String sql, List params)
//			throws PersistenceException {
//		if (obj == null) {
//			return new ArrayList(1);
//		}
//		// 读取Object中的值，然后拼装成SQL语句，再加上sql的条件，
//		return null;
//	}
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * 取SQL语句第一行第一列的Long类型返回值。如果没有检索到数据，则返回0； 如果检索的数据类型非长整类型，将抛出类型转换异常。
     */
    public long queryLong(String sql, List params) throws PersistenceException {
        String value = queryString(sql, params);
        if (StringUtils.isBlank(value)) {
            return 0;
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * 取SQL语句第一行第一列的 Integer 类型返回值。如果没有检索到数据，则返回0； 如果检索的数据类型非长整类型，将抛出类型转换异常。
     */
    public int queryInt(String sql, List params) throws PersistenceException {
        String value = queryString(sql, params);
        if (StringUtils.isBlank(value)) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 取SQL语句第一行第一列的String类型返回值。如果没有检索到数据，则返回空； 如果检索的数据类型非字符串类型，默认将其转换为字符串类型。
     */
    public String queryString(String sql, List params)
            throws PersistenceException {
        // 校验参数
        if (sql == null) {
            throw new BadParameterException("SQL语句都不能为空！");
        }
        // 构造处理结果方法
        SingleValueItem item = new SingleValueItem();
        query(sql, params, item);
        return item.getValue();
    }

    public void beginTransaction() throws PersistenceException {
        try {
            if (conn != null) {
                conn.setAutoCommit(false);
            // conn.setSavepoint();  // hql 暂时不支持savepont
            // conn
            }
        } catch (SQLException e) {
            throw new BadParameterException("开始事务时出现SQL异常", e);
        }
    }

    public void commit() throws PersistenceException {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new BadParameterException("事务提交时出现SQL异常", e);
        }
    }

    public void rollback() throws PersistenceException {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new BadParameterException("事务回滚时出现SQL异常", e);
        }
    }

    public void setAutoCommit(boolean autoCommit) throws PersistenceException {
        try {
            if (conn != null) {
                conn.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new BadParameterException("设置自动提交时出现SQL异常", e);
        }
    }

    public long count(Class clazz, String sqlWhere, List params) {
        if (clazz == null) {
            throw new BadParameterException("类参数不能为空。");
        }
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(mapping.getTableName(clazz));
        if (StringUtils.isNotEmpty(sqlWhere)) {
            if (!sqlWhere.toUpperCase().trim().startsWith("WHERE")) {
                sql.append(" WHERE ");
            }
            sql.append(sqlWhere);
        }
        return queryLong(sql.toString(), params);
    }

    public long readSequence(String sequenceName) throws PersistenceException {
        if (StringUtils.isEmpty(sequenceName)) {
            throw new BadParameterException("序列号不能为空！");
        }
        String sql = persistenceDialect.getSequenceSql(conn, sequenceName);
        return queryLong(sql, null);
    }
    private final static Object NULL_OBJECT = new Object();

    /*
     * 检查对象是否重复
     * @see com.xt.core.db.pm.PersistenceManager#duplicate(java.lang.Class, java.lang.String, java.util.List)
     */
    public boolean duplicate(final Class po, final String where,
            final List params) throws PersistenceException {
        StringBuilder sql = new StringBuilder("SELECT 1 FROM ");
        String tableName = mapping.getTableName(po);
        sql.append(tableName).append(" WHERE ").append(where);
        List list = query(sql.toString(), params, new Item() {

            public Object createObject(ResultSet rs) throws SQLException {
                return NULL_OBJECT;
            }

            public boolean isPagination() {
                return false;
            }
        });

        LogWriter.debug("duplicate list", list);

        return (list != null && !list.isEmpty());
    }

    public void close() throws PersistenceException {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new BadParameterException("关闭数据库链接时出现SQL异常！", e);
        }
    }
}

/**
 * 专门用于处理单值的查询结果。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-21
 */
class SingleValueItem implements Item {

    private String value;

    public SingleValueItem() {
    }

    public Object createObject(ResultSet rs) throws SQLException {
        value = rs.getString(1); // 只取第一个的第一个数据
        // while (rs.next());
        return value;
    }

    public String getValue() {
        return value;
    }

    public boolean isPagination() {
        return false;
    }
}

class NullItem implements Item {

    public NullItem() {
    }

    public Object createObject(ResultSet rs) throws SQLException {
        return this;
    }

    public boolean isPagination() {
        return false;
    }
}
