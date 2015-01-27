package com.xt.core.db.po;

import com.xt.core.db.po.impl.DialectDetection;
import com.xt.core.db.po.impl.NullDialect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import org.apache.log4j.Logger;

import com.xt.core.exception.POException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.CollectionUtils;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.PreparedStatementHelper;
import com.xt.core.utils.SqlUtils;
import com.xt.gt.sys.SystemConfiguration;
import java.util.Collections;

/**
 * <p>
 * Title: 框架类.
 * </p>
 * <p>
 * Description: 这个类是一个包装类，将实现IPO接口的对象保存到数据库中，其实现的方式与PO对象的机制是相同的。
 * </p>
 * 
 * @todo:还需要在对象里添加对象的属性，是已经被装载的，还是新对象，等等。
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * @author 郑伟
 * @version 1.0
 */
public class IPOBuilder {

    private final Logger logger = Logger.getLogger(IPOBuilder.class);
    
    /**
     * 在SQL语句中使用的关键字，这些关键字在作为表名称和字段名称时需要加上双引号“"”才能使用。
     * 必须要用同步的集合。
     */
    private static Set<String> keywords = Collections.synchronizedSet(new HashSet<String>());

    // 是否保存IPO对象的子对象，即级联保存（暂时没有使用）
    // private boolean cascade;

    // 真正要保存的对象（包含了所有对象的值）
    private final IPO ipo;

    // 表名称
    private String tableName;

    // 主键数组。
    private String[] primaryKeys;
    /**
     * 记录变动数据的状态信息。
     */
    private Map<String, Object> status;
    /**
     * 数据库方言对象。
     */
    private Dialect dialect;


    static {
        // 加载默认的关键字
        keywords.add("USER");
        keywords.add("FROM");
        keywords.add("SELECT");
        keywords.add("ORDER");
        keywords.add("GROUP");
        keywords.add("SORT");
        keywords.add("TABLE");
        keywords.add("BY");
        keywords.add("WHERE");
        keywords.add("DELETE");
        keywords.add("UPDATE");
        keywords.add("INTO");

        // 从配置文件中读取关键字
        String[] _keywords = SystemConfiguration.getInstance().readStrings("sql.KeyWorks");
        if (_keywords != null) {
            for (int i = 0; i < _keywords.length; i++) {
                keywords.add(_keywords[i]);
            }
        }
    // 检索此数据库的还“不”是 SQL92 关键字的所有 SQL 关键字的逗号分隔列表。
    // conn.getMetaData.getSQLKeywords()
    }

    public IPOBuilder(IPO ipo) {

        this.ipo = ipo;

        tableName = ipo.__getTableName();

        // 转换关键字
        tableName = convertKeyWord(tableName);

        primaryKeys = ipo.__getPrimaryKeys();

        status = ipo.__getStatus();

        // 用户定义了方言
        dialect = (Dialect) SystemConfiguration.getInstance().readObject("po.Dialect", null);


    }

    /**
     * 根据数据库信息确定指定的方言
     * @param conn
     */
    private void detectDriver(Connection conn) {
        if (this.dialect != null) {
            return;
        }
        // 自动检测
        if (SystemConfiguration.getInstance().readBoolean("po.Dialect.auto", false)) {
            try {
                String driverInfo = conn.getMetaData().getDriverName();
                dialect = DialectDetection.getInstance().getDialect(driverInfo);
            } catch (SQLException ex) {
                throw new POException("读取数据库源信息时出现异常。", ex);
            }
        }
        if (dialect == null) {
            dialect = new NullDialect();
        }
        LogWriter.debug(logger, "dialect", dialect);
    }

    /**
     * 如果表名称与某个关键字相同，则将其加上引号
     *
     * @param tableName
     * @return
     */
    private String convertKeyWord(String keyWord) {
        if (keyWord == null) {
            return null;
        }
        if (keywords.contains(keyWord)) {
            StringBuilder strBld = new StringBuilder("\"");
            strBld.append(keyWord).append("\"");
            return strBld.toString();
        }
        return keyWord;
    }

    public boolean insert(Connection conn) throws POException {
        // 校验主键是否为空
        validatePrimaryKeys();

        detectDriver(conn);

        boolean result = false;

        // 插入的属性的值
        List<Object> insertedParam = new ArrayList<Object>();

        PreparedStatement ps = null;
        try {

            String sqlStr = insertSqlBuilder(insertedParam);

            LogWriter.debug(logger, "insert=" + sqlStr);
            LogWriter.debug(logger, "SQL语句中的参数依次为： " + insertedParam.toString());

            // 生成PreparedStatementCreate对象
            ps = conn.prepareStatement(sqlStr);
            ps = prepareSqlParams(ps, insertedParam);

            // 只插入一条记录
            result = (ps.executeUpdate() == 1);

            // InputStream 的情况需要处理
            if (dialect != null) {
                dialect.insertAfter(ipo, conn, sqlStr, insertedParam);
            }

        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常", ex);
        } finally {
            status.clear();
            SqlUtils.closePreparedStatement(ps);
        }
        return result;
    }

    /**
     * 在更新，删除，通过主键查找，增加操作单条记录时，需要检验主键（数据库）的完整性，否则会引起数据库异常。 如果主键为空，则抛出异常
     *
     * @throws NullPrimaryKeyException
     */
    private void validatePrimaryKeys() throws NullPrimaryKeyException {
        // 如果主键为空,则不作检查
        if (null == primaryKeys || primaryKeys.length == 0) {
            return;
        }

        for (int i = 0; i < primaryKeys.length; i++) {
            String pk = primaryKeys[i];
            for (Iterator<String> iter = status.keySet().iterator(); iter.hasNext();) {
                String item = iter.next();
                // 如果主键的赋值等于空,则返回空
                if (pk.equals(item) && status.get(item) == null) {
                    throw new NullPrimaryKeyException();
                }
            }
        }
    }

    /**
     * 生成PreparedStatement实例，然后为PreparedStatement实例添加<br>
     * 属性（col中传递进来的）。最后返回PreparedStatement实例。
     *
     * @param ps
     *            准备语句
     * @param col
     *            结果集
     * @return PreparedStatement - 生成的PreparedStatement实例
     * @throws SQLException
     */
    protected PreparedStatement prepareSqlParams(PreparedStatement ps,
            Collection col) throws SQLException, POException {
        if (col == null || col.size() == 0) {
            return ps;
        }

        Iterator ii = col.iterator();
        int i = 1;
        while (ii.hasNext()) {
            Object value = ii.next();
            PreparedStatementHelper.setValue(ps, i++, value);
        }
        return ps;
    }

    /**
     * insert方法的SQL语句生成器,抽象方法，由实现类实现。该方<br>
     * 法返回一条insert SQL语句。
     *
     * @param updatedParam
     *            日期列的名称
     * @return String insert 方法的SQL语句。
     */
    protected String insertSqlBuilder(List<Object> insertedParam) throws SQLException {
        // 如果客户端没有调用setter方法给新添加的记录赋值，则返回空
        if (status.isEmpty()) {
            throw new POException("PO 对象中没有需要插入的数据！");
        }

        // 插入语句的字段和 VALUES 部分
        StringBuilder valuesSql = new StringBuilder();
        StringBuilder columnNamesSql = new StringBuilder();

        for (Iterator<String> iter = status.keySet().iterator(); iter.hasNext();) {
            String columnName = iter.next();
            Object value = status.get(columnName);
            if (dialect == null || !dialect.buildInsertColumn(ipo, columnName, value, columnNamesSql, valuesSql, insertedParam, iter.hasNext())) {
                columnNamesSql.append(columnName);
                valuesSql.append("?");
                insertedParam.add(value);
                if (iter.hasNext()) {
                    columnNamesSql.append(", ");
                    valuesSql.append(", ");
                }
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        sql.append(columnNamesSql);
        sql.append(") VALUES (");
        sql.append(valuesSql);
        sql.append(")");

        //LogWriter.debug(logger, "insertSqlBuilder=" + sql.toString());

        return sql.toString();
    }

    /**
     * 删除方法(根据主键删除，即删除前必须设置主键属性，否则，抛出空主键异常)<br>
     *
     * @param con -
     *            与数据库建立的连接
     * @return boolean 是否delete操作成功。true表示成功；false表示失败。
     * @throws POException
     */
    public boolean delete(Connection con) throws POException {
        // 主键必须赋值，否则抛出异常
        validatePrimaryKeys();

        detectDriver(con);

        boolean result = false;

        List values = new ArrayList(2); // 如果由非主键方式产生删除语句,将由此返回参数
        String sqlStr = deleteByPKSqlBuilder(values);
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sqlStr);
            // 准备参数
            if (values.size() == 0) {
                ps = preparePKParams(ps);
            } else {
                //LogWriter.debug(logger, "values=", values.size());
                ps = prepareSqlParams(ps, values);
            }

            // 只能删除一条记录
            result = (ps.executeUpdate() == 1);
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常", ex);
        } finally {
            status.clear();
            SqlUtils.closePreparedStatement(ps);
        }
        return result;
    }

    /**
     * SQL中delete语句的生成器。他根据主键来生成SQL语句。SQL语句应该是这样：<br>
     * DELETE FROM tableName WHERE pk1=? and pk2=? ; <br>
     * 如果由
     *
     * @return String delete语句
     */
    private String deleteByPKSqlBuilder(Collection values) {
        StringBuffer sqlStr = new StringBuffer("DELETE FROM ");
        sqlStr.append(tableName);

        String where = pkWhereSqlBuilder();

        // 一个表没有主键的情况会产生Where为空的情况
        // 尝试使用所有数据进行拼装
        if (where == null) {
            if (status.isEmpty() || values == null) {
                throw new POException("不能使用此方法删除整个表的数据!");
            }

            StringBuffer strBuf = new StringBuffer(" WHERE 1=1 ");
            for (Iterator iter = status.keySet().iterator(); iter.hasNext();) {
                String item = (String) iter.next();
                Object value = status.get(item);
                // Lob 类型的值不能拼装为条件
                if (SqlUtils.isLob(value)) {
                    continue;
                }
                values.add(value);
                strBuf.append(" AND ").append(item).append("=?");

            }
            where = strBuf.toString();
        }

        sqlStr.append(where);
        return sqlStr.toString();
    }

    /**
     * 返回删除,更新语句的Where部分(根据主键).
     *
     * @return String 删除,更新语句的Where部分(带有Where);如果此表没有主键,则返回空.
     */
    private String pkWhereSqlBuilder() {
        /**
         * 如果此表没有主键,则返回空
         */
        if (null == primaryKeys || primaryKeys.length == 0) {
            return null;
        }

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" WHERE 1=1 ");
        for (int i = 0; i < primaryKeys.length; i++) {
            sqlStr.append(" AND ");
            String pk = primaryKeys[i];
            sqlStr.append(pk).append("=? ");
        }
        return sqlStr.toString();
    }

    /**
     * 生成PreparedStatement实例，然后为PreparedStatement实例添加<br>
     * 属性（data中传递进来的）。最后返回PreparedStatement实例。<br>
     * PreparedStatement由参数ps生成。 sqlParam中的全部属性。
     *
     * @param ps
     *            准备语句
     * @return PreparedStatement - 生成的PreparedStatement实例
     * @throws SQLException
     */
    protected PreparedStatement preparePKParams(PreparedStatement ps)
            throws SQLException, POException {
        ArrayList pkValues = new ArrayList(primaryKeys.length); // 主键对应的值
        for (int i = 0; i < primaryKeys.length; i++) {
            String pk = primaryKeys[i];
//			LogWriter.debug(logger, "pk", pk);
            for (Iterator iter = ipo.__getStatus().keySet().iterator(); iter.hasNext();) {
                String item = (String) iter.next();
                // 如果主键的赋值等于空,则返回空
                if (pk.equals(item)) {
                    pkValues.add(status.get(item));
                }
            }
        }
        return prepareSqlParams(ps, pkValues);
    }

    /**
     * 将这个 BaseBpo实例代表的数据项更新到数据库中。
     *
     * @param con -
     *            与数据库建立的连接
     * @return boolean 是否update操作成功。true表示成功；false表示失败。
     * @throws POException
     */
    public boolean update(Connection conn) throws POException {

        // 主键不能为空，否则抛出异常
        validatePrimaryKeys();

        detectDriver(conn);

        // 更新后的属性的值
        List updatedParam = new ArrayList();
        boolean result = false;

        PreparedStatement ps = null;

        try {
            // 生成updata的SQL语句
            String sqlStr = updateSqlBuilder(pkWhereSqlBuilder(), updatedParam);

            LogWriter.debug(logger, "SQL语句： " + sqlStr);
            LogWriter.debug(logger, "参数依次为： " + updatedParam);

            // 没有被修改的项，不需要执行更新操作，返回操作成功；
            if (sqlStr == null) {
                return false;
            }

            ps = conn.prepareStatement(sqlStr);
            // 准备参数
            ps = prepareSqlParams(ps, updatedParam);

            // 更新一条记录
            result = (ps.executeUpdate() == 1);
        } catch (SQLException ex) {
            throw new POException("00039", ex);
        } finally {
            status.clear();
            SqlUtils.closePreparedStatement(ps);
        }
        return result;
    }

    /**
     * @return String 生成更新数据表的SQL语句
     */
    protected String updateSqlBuilder(String where, List updatedParam) throws SQLException {
        // 如果没有要更新的数据,返回空
        if (status == null || status.size() == 0) {
            return null;
        }

        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");

        for (Iterator iter = status.keySet().iterator(); iter.hasNext();) {
            String columnName = (String) iter.next();
            // 将主键排除在外
            if (CollectionUtils.indexOf(primaryKeys, columnName) > -1) {
                continue;
            }
            Object value = status.get(columnName);

            // 处理 Clob 和Blob 的类型
            if (dialect == null || !dialect.buildUpdateColumn(ipo, columnName, value, sql, updatedParam, iter.hasNext())) {
                sql.append(columnName).append("=?");
                updatedParam.add(status.get(columnName));
                if (iter.hasNext()) {
                    sql.append(", ");
                }
            }
        }

        // 生成主键的参数(根据主键形成删除的条件)
        for (int i = 0; i < primaryKeys.length; i++) {
            for (Iterator iter = status.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                if (primaryKeys[i].equals(name)) {
                    updatedParam.add(status.get(name));
                    break;
                }
            }
        }

        if (where != null) {
            sql.append(where);
        }
        return sql.toString();
    }

    /**
     * 通过主键查找，返回唯一的事例，并将查询结果赋予本身
     *
     * @param conn
     *            数据库连接
     * @throws POException
     */
    public IPO findByPK(Connection conn) throws POException {
        if (null == primaryKeys || primaryKeys.length == 0) {
            throw new NullPrimaryKeyException("根据主键查询时未定义任何主键");
        }

        // 主键必须赋值，否则抛出异常
        validatePrimaryKeys();

        detectDriver(conn);

        // 根据主键形成查询语句
        String sql = findByPKSqlBuilder();
        PreparedStatement ps = null;
        try {
            // 得到准备连接语句
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // 准备参数
            preparePKParams(ps);

            // 得到结果集
            ResultSet rs = ps.executeQuery();

            // 如果有查询结果，将创建一个新的对象，否则返回空
            if (rs.next()) {
                IPO newIPO = (IPO) ClassHelper.newInstance(ipo.getClass());
                newIPO.__load(rs, newIPO);
                return newIPO;
            }
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常", ex);
        } finally {
            SqlUtils.closePreparedStatement(ps);
        }
        return null;
    }

    /**
     * SQL中findByPK语句的生成器。他根据主键来生成SQL语句,只删除一条记录。SQL语句应该是这样： SELECT * FROM
     * tableName WHERE pk1=? and pk2=? ;
     *
     * @return String delete语句
     */
    private String findByPKSqlBuilder() {
        StringBuffer sqlStr = new StringBuffer("SELECT * FROM ");
        sqlStr.append(tableName);
        String where = pkWhereSqlBuilder();

        if (where != null) {
            sqlStr.append(where);
        }
        return sqlStr.toString();
    }

    /**
     * 删除方法(根据条件删除)。<br>
     *
     * @param con -
     *            与数据库建立的连接
     * @param where
     *            条件语句（不带有关键字“where”）,可以用问号代替其值, 如果此参数为空,删除整个表的数据.
     * @return boolean 是否delete操作成功。true表示成功；false表示失败。
     * @throws POException
     */
    public int delete(String where, List params, Connection conn)
            throws POException {

        detectDriver(conn);

        String sqlStr = deleteSqlBuilder(where);
        LogWriter.debug(logger, sqlStr);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlStr);
            // 准备参数
            prepareSqlParams(ps, params);
            LogWriter.debug(logger, "SQL语句中的参数依次为： " + params);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常。", ex);
        } finally {
            status.clear();
            SqlUtils.closePreparedStatement(ps);
        }
    }

    /**
     * SQL中delete语句的生成器。根据主键来生成SQL语句。SQL语句应该是这样：<br>
     * DELETE FROM tableName WHERE pk1=? and pk2=? ; <br>
     * 如果sqlWhere属性为null，则删除这个表中的所有数据项。
     *
     * @param where
     *            条件语句（不必带有关键字“where”）
     * @return String delete语句
     */
    private String deleteSqlBuilder(String where) {
        StringBuffer sqlStr = new StringBuffer("DELETE FROM ");
        sqlStr.append(tableName);
        if (where != null) {
            sqlStr.append(" WHERE 1=1 ").append(where);
        }
        // LogWriter.info(logger, sqlStr.toString());
        return sqlStr.toString();
    }

    /**
     * 根据sqlWhere条件的查询,返回满足条件的所有数据项。<br>
     * findSQLBuilder方法生成sql 语句，调用<br>
     * PreparedStatementCreate方法生成PreparedStatement实例。<br>
     * 查询结束后要调用resultSetClose方法和<br>
     * PreparedStatementClose方法。最后，方法调用<br>
     * loadListFromResultSet方法生成一个DataObjectList对象，并返回。<br>
     *
     * @param con -
     *            与数据库建立的连接
     * @param isCount
     *            是否计算查询结果的总数
     * @param sqlWhere
     *            查询的条件部分（不需要关键字where），如果为空则查询所有结果；
     * @param params
     *            查询的条件中的参数
     * @param sqlOrder
     *            查询的排序部分（不需要关键字group by），如果为空则不进行排序；
     * @return QueryResultList 得到的结果集，封装在一个QueryResultList实例中
     * @throws POException
     */
    public QueryResultList findAll(Connection con, boolean isCount,
            String sqlWhere, List params, String sqlOrder) throws POException {
        detectDriver(con);

        int totalCount = 0;

        String sqlStr = findSqlBuilder(sqlWhere, sqlOrder);
        LogWriter.debug(logger, "findAll sqlStr=" + sqlStr);
        LogWriter.debug(logger, "SQL语句中的参数依次为： " + params);

        ResultSet countRs = null; // 用于计算查询结果总数的结果集
        ResultSet rs = null;
        PreparedStatement ps = null;
        QueryResultList result = null;

        try {
            // 得到总数
            if (isCount) {
                String sqlCount = countSqlBuilder(sqlWhere);
                LogWriter.debug(logger, "Select count 语句 ： " + sqlCount);
                ps = con.prepareStatement(sqlCount);
                ps = prepareSqlParams(ps, params);
                countRs = ps.executeQuery();
                if (countRs.next()) {
                    totalCount = countRs.getInt(1);
                } else {
                    totalCount = -1;
                }
                LogWriter.debug(logger, "totalCount ： " + totalCount);
            }
            // 得到结果
            ps = con.prepareStatement(sqlStr,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ps = prepareSqlParams(ps, params);

            rs = ps.executeQuery();
            result = loadListFromResultSet(rs, totalCount);
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常。", ex);
        } finally {
            // cleanSql();
            SqlUtils.closeResultSet(countRs);
            SqlUtils.closeResultSet(rs);
            SqlUtils.closePreparedStatement(ps);
        }
        return result;
    }

    /**
     * load和findAll方法的SQL语句生成方法，用来产生select的SQL语句。<br>
     * 该方法会根据sqlWhere,sqlOrder和<br>
     * tableName属性生成SQL语句。结果总是返回数据项中的全部属性；<br>
     * tableName属性表示from；sqlWhere表示where，如果sqlWhere为<br>
     * null，表示返回所有数据项；sqlOrder表示order by，如果为null，<br>
     * 则不排序。 SQL语句大体应该是这样生成的： SELECT * FROM tableName WHERE sqlWhere ORDER BY
     * sqlOrder
     *
     * @param sqlWhere
     *            查询的条件部分（不需要关键字where）
     * @param sqlOrder
     *            查询的排序部分（不需要关键字order by）
     * @return String 返回生成的SQL语句。
     */
    protected String findSqlBuilder(String sqlWhere, String sqlOrder) {
        StringBuffer sqlStr = new StringBuffer("SELECT * FROM ");
        sqlStr.append(tableName);

        /** ****根据查询条件和数据权限形成SQL语句****** */
        // 如果查询条件为null，则返回所有项
        if (sqlWhere != null && sqlWhere.trim().length() > 0) {
            sqlStr.append(" WHERE 1=1 AND ");
            sqlStr.append(sqlWhere);
        }

        /** ************根据查询条件和数据权限形成SQL语句********************* */
        if (sqlOrder != null && sqlOrder.trim().length() > 0) {
            // 如果排序条件为null，则不排序
            sqlStr.append(" ORDER BY ");
            sqlStr.append(sqlOrder);
        }

        return sqlStr.toString();
    }

    /**
     * 该方法用来生成满足sqlWhere条件数据项的总数的SQL语句。方法会用到sqlWhere属性。
     *
     * @return String 生成的SQL语句。
     */
    protected String countSqlBuilder(String sqlWhere) {
        StringBuffer sqlStr = new StringBuffer("SELECT COUNT(*) FROM ");
        sqlStr.append(tableName);
        if (sqlWhere != null && sqlWhere.trim().length() > 0) {
            sqlStr.append(" WHERE ");
            sqlStr.append(sqlWhere);
        }
        return sqlStr.toString();
    }

    /**
     * 该方法用来生成满足sqlWhere条件数据项的总数的SQL语句。方法会用到sqlWhere属性。
     *
     * @return String 生成的SQL语句。
     */
    public int count(String sqlWhere, Connection conn) throws POException {
        int result = 0;

        detectDriver(conn);

        String sqlStr = countSqlBuilder(sqlWhere);
        Statement ps = null;
        try {
            ps = conn.createStatement();
            ResultSet rs = ps.executeQuery(sqlStr);
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("SQL 异常", ex);
        } finally {
            SqlUtils.closeStatement(ps);
        }
        return result;

    }

    /**
     * 该类用于将查询得到的ResultSet对象<br>
     * 封装成一个DataObjectList对象。
     *
     * @param rs
     *            被封装的查询结果。
     * @return ResultSetList 查询结果列表
     * @throws POException
     */
    protected QueryResultList loadListFromResultSet(ResultSet rs, int totalCount)
            throws SQLException {
        int fetchSize = ipo.getFetchSize();
        // 生成一个DataObjectList对象，用来保存返回结果；
        QueryResultList result = new QueryResultList();
        int startIndex = ipo.getStartIndex();
        result.setItemsPerPage(fetchSize);
        result.setTotalItems(totalCount);
        result.setStartIndex(startIndex);
        IPO newIPO = (IPO) ClassHelper.newInstance(ipo.getClass());

        // 如果要求分页显示
        if (fetchSize > 0) {

            // 将查询结果集的游标跳转到指定的位置
            boolean _status = jumpTo(startIndex + 1, rs);

            // 生成itemsPerPage个bpo来保存查询得到的记录
            if (_status) {
                for (int i = 0; i < fetchSize; i++) {
                    // 初始化对象，并装入结果列表
                    Object entity = ClassHelper.newInstance(ipo.__getEntityClass());
                    newIPO.__load(rs, entity);
                    result.addItem(entity);
                    if (!rs.next()) {
                        // 如果当前记录已经是最后一条，跳出循环
                        break;
                    }
                }
            }
        } else {
            // 不要求分页显示,取得全部结果
            while (rs.next()) {
                Object entity = ClassHelper.newInstance(ipo.__getEntityClass());
                newIPO.__load(rs, entity);
                result.addItem(entity);
            }
        }
        LogWriter.debug(logger, "loadListFromResultSet.result.getBpoList().size()=" + result.getResultList().size());
        return result;
    }

    /**
     * 将记录调整到指定的位置
     *
     * @param rowNum
     *            int 游标跳过的行数
     * @param rs
     *            ResultSet 结果集
     * @return boolean 如果指定的位置小于结果集中记录的条数,返回真;否则,返回假.
     */
    private boolean jumpTo(int rowNum, ResultSet rs) throws SQLException {

        // 使用ODBC连接数据库时，不能使用记录定位方法
        // //将指针移动到的一条记录 ；
        // rs.first();
        // //将ResultSet的指针跳转到第rowNum 条数据
        // return rs.absolute(rowNum);
        boolean _status = true;
        for (int i = 0; i < rowNum; i++) {
            if (!(_status = rs.next())) {
                break;
            }
        }
        return _status;
    }

    /**
     * 根据传入的条件进行更新数据表的操作.更新的新值为其实现类的属性值.其缺点是不能进行批处理更新操作, 在性能上会有一些影响.<br>
     *
     * @param con -
     *            与数据库建立的连接
     * @param where
     *            条件语句（不能带有关键字“where”）
     * @param params
     *            条件语句中的参数（其顺序要与条件语句中的?对应）
     * @return int 被更新的记录条数。
     * @throws POException
     */
    public int update(String where, List params, Connection con)
            throws POException {

        detectDriver(con);

        // 更新后的值
        List updatedParams = new ArrayList();

        PreparedStatement ps = null;

        try {
            // 生成updata的SQL语句
            String sqlStr = updateSqlBuilder(" WHERE 1=1 AND " + where,
                    updatedParams);

            ps = con.prepareStatement(sqlStr);
            // 将更新的值与更新条件的参数合成到一起
            if (params != null) {
                updatedParams.addAll(params);
            }
            if (dialect != null) {
                dialect.updateAfter(ipo, con, sqlStr, params);
            }

            // 准备参数
            ps = prepareSqlParams(ps, updatedParams);
            LogWriter.debug(logger, "SQL语句为： " + sqlStr);
            LogWriter.debug(logger, "参数依次为： " + updatedParams.toString());
            // 返回变化的行数
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new POException("SQL 异常", ex);
        } finally {
            status.clear();
            SqlUtils.closePreparedStatement(ps);
        }
    }
}
/**
ABSOLUTE 	ANY
ADD 	ARE
ADMINDB 	AS
ALL 	ASC
Alphanumeric
ALTER 	AUTHORIZATION
ALTER TABLE 	AUTOINCREMENT — See COUNTER
And 	Avg
AS

B-C
BEGIN 	COLLATION
Between 	COLUMN
BINARY 	COMMIT
BIT 	COMP, COMPRESSION
BIT_LENGTH 	CONNECT
BOOLEAN — See BIT 	CONNECTION
BOTH 	CONSTRAINT, CONSTRAINTS
BY 	CONTAINER
BYTE 	CONTAINS
CASCADE 	CONVERT
CATALOG 	Count
CHAR, CHARACTER — See TEXT 	COUNTER
CHAR_LENGTH 	CREATE
CHARACTER_LENGTH 	CURRENCY
CHECK 	CURRENT_DATE
CLOSE 	CURRENT_TIME
CLUSTERED 	CURRENT_TIMESTAMP
COALESCE 	CURRENT_USER
COLLATE 	CURSOR

D
DATABASE 	DISALLOW
DATE — See DATETIME 	DISCONNECT
DATETIME 	DISTINCT
DAY 	DISTINCTROW
DEC, DECIMAL 	DOMAIN
DECLARE 	DOUBLE
DELETE 	DROP
DESC

E-H
Eqv 	FOREIGN
EXCLUSIVECONNECT 	FROM
EXEC, EXECUTE 	FROM 子句
EXISTS 	GENERAL — See LONGBINARY
EXTRACT 	GRANT
FALSE 	GROUP
FETCH 	GUID
FIRST 	HAVING
FLOAT，FLOAT8 — 参阅 DOUBLE 	HOUR
FLOAT4 — See SINGLE

I
IDENTITY 	INPUT
IEEEDOUBLE — See DOUBLE 	INSENSITIVE
IEEESINGLE — See SINGLE 	INSERT
IGNORE 	INSERT INTO
IMAGE 	INT，INTEGER，INTEGER4 — 参阅 LONG
Imp 	INTEGER1 — 参阅 BYTE
In 	INTEGER2 — 参阅 SHORT
IN 	INTERVAL
INDEX 	INTO
INDEXCREATEDB 	Is
INNER 	ISOLATION

J-M
JOIN 	LONGTEXT
KEY 	LOWER
LANGUAGE 	MATCH
LAST 	Max
LEFT 	MEMO — 参阅 LONGTEXT
Level* 	Min
Like 	MINUTE
LOGICAL，LOGICAL1 — 参阅 BIT 	Mod
LONG 	MONEY — 参阅 CURRENCY
LONGBINARY 	MONTH
LONGCHAR

N-P
NATIONAL 	Outer*
NCHAR 	OUTPUT
NONCLUSTERED 	OWNERACCESS
Not 	PAD
NTEXT 	PARAMETERS
NULL 	PARTIAL
NUMBER — See DOUBLE 	PASSWORD
NUMERIC — See DECIMAL 	PERCENT
NVARCHAR 	PIVOT
OCTET_LENGTH 	POSITION
OLEOBJECT — See LONGBINARY 	PRECISION
ON 	PREPARE
OPEN 	PRIMARY
OPTION 	PRIVILEGES
Or 	PROC, PROCEDURE
ORDER 	PUBLIC

Q-S
REAL — See SINGLE 	SMALLDATETIME
REFERENCES 	SMALLINT — See SHORT
RESTRICT 	SMALLMONEY
REVOKE 	SOME
RIGHT 	SPACE
ROLLBACK 	SQL
SCHEMA 	SQLCODE, SQLERROR, SQLSTATE
SECOND 	StDev
SELECT 	StDevP
SELECTSCHEMA 	STRING — See TEXT
SELECTSECURITY 	SUBSTRING
SET 	Sum
SHORT 	SYSNAME
SINGLE 	SYSTEM_USER
SIZE

T-Z
TABLE 	UPDATEOWNER
TableID* 	UPDATESECURITY
TEMPORARY 	UPPER
TEXT 	USAGE
TIME — See DATETIME 	USER
TIMESTAMP 	USING
TIMEZONE_HOUR 	VALUE
TIMEZONE_MINUTE 	VALUES
TINYINT 	Var
TO 	VARBINARY — 参阅 BINARY
TOP 	VARCHAR — 参阅 TEXT
TRAILING 	VarP
TRANSACTION 	VARYING
TRANSFORM 	VIEW
TRANSLATE 	WHEN
TRANSLATION 	WHENEVER
TRIM 	WHERE
TRUE 	WITH
UNION 	WORK
UNIQUE 	Xor
UNIQUEIDENTIFIER 	YEAR
UNKNOWN 	YESNO — See BIT
UPDATE 	ZONE
 */
