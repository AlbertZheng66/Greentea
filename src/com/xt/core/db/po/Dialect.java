
package com.xt.core.db.po;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 用于根据数据库的情况来确定ＳＱＬ语句的拼装，处理和执行情况。
 * @author albert
 */
public interface Dialect {

    public boolean buildInsertColumn(IPO ipo, String columnName, Object value,
            StringBuilder sql, StringBuilder valueSql, List params, boolean hasNext) throws SQLException ;

     public boolean buildUpdateColumn(IPO ipo, String columnName, Object value,
            StringBuilder sql, List params, boolean hasNext) throws SQLException ;

    public void insertAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException ;

    public void updateAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException ;

    /**
     * 返回是否采用自动分页的方式进行分页，如果返回true，将采用系统自动分页的方式，
     * 即数行的方式进行分页，否则将采用buildPaginationSql 自动创建的方式进行分页。
     * @param sql
     * @param startIndex
     * @param maxItems
     * @return
     */
    public boolean autoPagination(String sql, int startIndex, int fetchSize);

    /**
     * 根据已有 SQL,构建翻页用的 SQL 语句。
     * @param sql        原始的SQL语句
     * @param startIndex 开始的记录数，从 0 开始计数
     * @param fetchSize 提交的最大数量
     * @return 转换后的 SQL
     */
    public String buildPaginationSql(String sql, int startIndex, int fetchSize);

}
