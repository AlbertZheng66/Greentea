package com.xt.core.db.po.impl;

import com.xt.core.db.po.Dialect;
import com.xt.core.db.po.IPO;
import com.xt.core.exception.POException;
import com.xt.core.utils.IOHelper;
import com.xt.core.utils.PreparedStatementHelper;
import com.xt.core.utils.SqlUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

/**
 * 默认的支持 Oracle 数据库的方言。
 * @author albert
 */
public class OracleDialect implements Dialect {

    public boolean buildInsertColumn(IPO ipo, String columnName, Object value, StringBuilder columnNamesSql,
            StringBuilder valueSql, List params, boolean hasNext) throws SQLException {
        if (SqlUtils.isBlob(value)) {
            columnNamesSql.append(columnName);
            valueSql.append("empty_blob()");
            if (hasNext) {
                columnNamesSql.append(", ");
                valueSql.append(", ");
            }
            return true;
        } else if (SqlUtils.isClob(value)) {
            columnNamesSql.append(columnName);
            valueSql.append("empty_clob()");
            if (hasNext) {
                columnNamesSql.append(", ");
                valueSql.append(", ");
            }
            return true;
        }
        return false;
    }

    public boolean buildUpdateColumn(IPO ipo, String columnName, Object value, StringBuilder sql, List params, boolean hasNext) throws SQLException {
        if (value == null) {
            return false;
        }
        if (SqlUtils.isBlob(value)) {
            sql.append(columnName).append(" = empty_blob()");
            if (hasNext) {
                sql.append(", ");
            }
            return true;
        } else if (SqlUtils.isClob(value)) {
            sql.append(columnName).append(" = empty_clob()");
            if (hasNext) {
                sql.append(", ");
            }
            return true;
        }
        return false;
    }

    public void insertAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException {
        // 插入所有的 Lob字段
        process(ipo, conn);
    }

    public void updateAfter(IPO ipo, Connection conn, String sql, List params) throws SQLException {
        // 插入所有的Lob字段
        process(ipo, conn);
    }

    protected void process(IPO ipo, Connection conn) throws SQLException {
        for (Iterator<Map.Entry<String, Object>> iter = ipo.__getStatus().entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, Object> entry = iter.next();
            if (SqlUtils.isBlob(entry.getValue())) {
                writeBlob(ipo, conn, ipo.__getTableName(), entry.getKey(), entry.getValue());
            } else if (SqlUtils.isClob(entry.getValue())) {
                writeClob(ipo, conn, ipo.__getTableName(), entry.getKey(), entry.getValue());
            }
        }
    }

    protected void writeBlob(IPO ipo, Connection conn, String tableName, String columnName, Object value) throws SQLException {
        // 创建结果集
        ResultSet rs = createResultSet(columnName, tableName, ipo, conn);

        if (rs.next()) {
            /* 取出此 BLOB 对象 */
            BLOB blob = (BLOB) rs.getBlob(columnName);
            BufferedOutputStream out = new BufferedOutputStream(blob.getBinaryOutputStream());
            InputStream in = null;
            /* 向 BLOB 对象中写入数据 */
            if (value instanceof byte[]) {
                in = new ByteArrayInputStream((byte[]) value);
            } else if (value instanceof InputStream) {
                in = (InputStream) value;
            } else if (value instanceof Blob) {
                in = ((Blob) value).getBinaryStream();
            } else {
                throw new POException(String.format("不能处理的值的类型[%s]", value.getClass().toString()));
            }
            IOHelper.i2o(in, out, true, true);
        }
    }

    protected void writeClob(IPO ipo, Connection conn, String tableName, String columnName, Object value) throws SQLException {
        // 创建结果集
        ResultSet rs = createResultSet(columnName, tableName, ipo, conn);
        while (rs.next()) {
            /* 取出此CLOB对象 */
            CLOB clob = (CLOB) rs.getClob(columnName);

            /* 向CLOB对象中写入数据 */
            BufferedWriter writer = new BufferedWriter(clob.getCharacterOutputStream());
            Reader reader = null;
            /* 向 BLOB 对象中写入数据 */
            if (value instanceof Reader) {
                reader = ((Reader) value);
            } else if (value instanceof Clob) {
                reader = ((Clob) value).getCharacterStream();
            } else {
                throw new POException(String.format("不能处理的值的类型[%s]", value.getClass().toString()));
            }
            IOHelper.r2w(reader, writer, true, true);
        }
    }

    public boolean autoPagination(String sql, int startIndex, int fetchSize) {
        if (startIndex == 0) {
            return true;
        }
        return false;
    }

    public String buildPaginationSql(String sql, int startIndex, int fetchSize) {
        StringBuilder pageSql = new StringBuilder();
        pageSql.append("select * from (").append("select t.*, rownum rownum_ from (").append(sql).append(") t where rownum <= ").append(startIndex + fetchSize).append(") t where t.rownum_ > ").append(startIndex);
        return pageSql.toString();
    }

    private ResultSet createResultSet(String columnName, String tableName, IPO ipo, Connection conn) throws SQLException {
        List params = new ArrayList(1);
        StringBuilder sql = new StringBuilder("SELECT ");
        sql.append(columnName).append(" FROM ").append(tableName).append(" WHERE ");
        // 构建主键部分 ID='111'；
        for (int i = 0; i < ipo.__getPrimaryKeys().length; i++) {
            String pkName = ipo.__getPrimaryKeys()[i];
            sql.append(pkName).append("=?,");
            params.add(ipo.__getStatus().get(pkName));
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" FOR UPDATE");
        PreparedStatement pStmt = conn.prepareStatement(sql.toString());
        for (int i = 0; i <= params.size(); i++) {
            Object value = params.get(i);
            PreparedStatementHelper.setValue(pStmt, i + 1, value);
        }
        ResultSet rs = pStmt.executeQuery();
        return rs;
    }
}
