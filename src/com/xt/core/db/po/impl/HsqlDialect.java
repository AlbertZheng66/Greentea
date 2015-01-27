package com.xt.core.db.po.impl;

import com.xt.core.db.po.Dialect;
import com.xt.core.db.po.IPO;
import com.xt.core.exception.POException;
import com.xt.core.utils.IOHelper;
import com.xt.core.utils.SqlUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import org.hsqldb.jdbc.jdbcBlob;
import org.hsqldb.jdbc.jdbcClob;

/**
 * 默认的支持 HSQL 数据库的方言。
 * @author albert
 */
public class HsqlDialect implements Dialect {

    public boolean buildInsertColumn(IPO ipo, String columnName, Object value, StringBuilder sql, StringBuilder valueSql, List params, boolean hasNext) throws SQLException {
        if (SqlUtils.isBlob(value)) {
            sql.append(columnName).append(", ");
            valueSql.append("?, ");
            processBlob(value, params);
        } else if (SqlUtils.isClob(value)) {
            sql.append(columnName).append(",");
            valueSql.append("?, ");
            processClob(value, params);
            return true;
        }
        return false;
    }

    public boolean buildUpdateColumn(IPO ipo, String columnName, Object value, StringBuilder sql, List params, boolean hasNext) throws SQLException {
        if (value == null) {
            return false;
        }
        if (SqlUtils.isBlob(value)) {
            sql.append(columnName).append("=?");
            if (hasNext) {
                sql.append(", ");
            }
            processBlob(value, params);
            return true;
        } else if (SqlUtils.isClob(value)) {
            sql.append(columnName).append("=?");
            if (hasNext) {
                sql.append(", ");
            }
            processClob(value, params);
            return true;
        }
        return false;
    }

    protected void processBlob(Object value, List params) throws SQLException {
        if (value instanceof byte[]) {
            params.add(new org.hsqldb.jdbc.jdbcBlob((byte[]) value));
        } else if (value instanceof InputStream) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            try {
                IOHelper.i2o((InputStream) value, baos, true, false);
                params.add(new org.hsqldb.jdbc.jdbcBlob(baos.toByteArray()));
                baos.close();
            } catch (IOException e) {
                throw new SQLException("写输入流是异常", e);
            }
        } else if (value instanceof jdbcBlob) {
            params.add((jdbcBlob) value);
        } else {
            throw new POException(String.format("不能处理的值的类型[%s]", value.getClass().toString()));
        }
    }

    protected void processClob(Object value, List params) throws SQLException {
        if (value instanceof Reader) {
            StringWriter writer = new StringWriter(1024);
            try {
                IOHelper.r2w((Reader) value, writer, true, false);
                params.add(new jdbcClob(writer.toString()));
                writer.close();
            } catch (IOException e) {
                throw new SQLException("写输入流是异常", e);
            }
        } else if (value instanceof jdbcClob) {
            params.add((jdbcClob) value);
        } else {
            throw new POException(String.format("不能处理的值的类型[%s]", value.getClass().toString()));
        }
    }

    public void insertAfter(IPO ipo, Connection conn, String sql, List params) {
    }

    public void updateAfter(IPO ipo, Connection conn, String sql, List params) {
    }

    /**
     * 如果字符串不是以“SELECT”开头，则使用自动分页，否则使用分页的SQL语句.
     * @param sql
     * @param startIndex
     * @param fetchSize
     * @return
     */
    public boolean autoPagination(String sql, int startIndex, int fetchSize) {
        Pattern pattern = Pattern.compile("^SELECT\\b+.*$", Pattern.CASE_INSENSITIVE);
        return !(pattern.matcher(sql).matches());
    }

    public String buildPaginationSql(String sql, int startIndex, int fetchSize) {
        StringBuilder pageSql = new StringBuilder();
        if (startIndex == 0) {
            pageSql.append("SELECT TOP ").append(fetchSize);
        } else {
            pageSql.append("SELECT LIMIT ").append(startIndex).append(" ").append(fetchSize).append(" ");
        }
        // 注意：如果不是以“SELECT”开头的语句，比如：Union等，不能处理
        pageSql.append(sql.trim().substring("SELECT".length()));
        return pageSql.toString();
    }
}
