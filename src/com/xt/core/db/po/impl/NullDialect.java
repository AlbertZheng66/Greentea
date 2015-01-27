
package com.xt.core.db.po.impl;

import com.xt.core.db.po.Dialect;
import com.xt.core.db.po.IPO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author albert
 */
public class NullDialect implements Dialect {

    public boolean buildInsertColumn(IPO ipo, String columnName, Object value, StringBuilder sql, StringBuilder valueSql, List params, boolean hasNext) throws SQLException {
        return false;
    }

    public boolean buildUpdateColumn(IPO ipo, String columnName, Object value, StringBuilder sql, List params, boolean hasNext) throws SQLException {
        return false;
    }

    public void insertAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException {
    }

    public void updateAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException {
    }

    public boolean autoPagination(String sql, int startIndex, int fetchSize) {
        return true;
    }

    public String buildPaginationSql(String sql, int startIndex, int fetchSize) {
        return sql;
    }

}
