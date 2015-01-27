package com.xt.core.db.meta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.conn.DatabaseContextManager;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.SystemConfiguration;
import java.util.Collections;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 提供表的所有元信息. <p> Title: XT框架-事务逻辑部分 </p> <p> Description:
 * 提供并缓存了所有数据库表的元信息。一般情况下，在应用程序启动（如Servlet启动） 时对其进行加载。然后可在不同的地方进行调用。
 * 注意：表名称和所有的列名称将转换为大写！ </p> <p> Copyright: Copyright (c) 2006 </p> <p> Company:
 * </p>
 *
 * @author 郑伟
 * @version 1.0 @date 2006-9-10
 */
public class Database {

    private final Logger logger = Logger.getLogger(Database.class);
    private String schema;
    /**
     * 数据库信息是否已经被装置。
     */
    private boolean loaded = false;
    /**
     * 存放所有的数据库元信息
     */
    private final Map<String, Table> tables = Collections.synchronizedMap(new HashMap<String, Table>());
    private static Database instance = new Database();
    /**
     * load a table when you can not find it
     */
    private boolean lazyLoading = SystemConfiguration.getInstance().readBoolean("database.lazyLoading", true);

    private Database() {
    }

    public static Database getInstance() {
        return instance;
    }

    /**
     * 返回所有的表名称
     *
     * @return
     */
    public Set getTableNames() {
        return tables.keySet();
    }

    /**
     * 通过表名称返回相应的表的原信息
     *
     * @param tableName 不区分大小写 String
     * @param schema 不区分大小写,如果为空,则返回装载时默认的schema String
     * @return TableMeta
     */
    public Table find(String _schema, String tableName) {
        if (null == tableName) {
            return null;
        }

        // 没有指定这采用全局，全局也没有返回空；然后将其转换为大写
        _schema = (null == _schema) ? (this.schema == null ? null : this.schema.toUpperCase()) : _schema.toUpperCase();

        // 全名称
        String fullName = _schema == null ? tableName.toUpperCase() : (_schema
                + "." + tableName.toUpperCase());

        LogWriter.debug(logger, "find tableName", fullName);
        Table table = tables.get(fullName);

        if (table == null && lazyLoading) {
            DatabaseContext databaseContext = DatabaseContextManager.getInstance().getDefaultDatabaseContext();
            load(databaseContext, tableName);
        }

        return table;
    }

    /**
     * 通过数据库连接装载所有的表的元信息
     *
     * @param conn Connection 数据库连接
     * @return TableMeta
     */
    synchronized public void load(DatabaseContext dc) {
        load(dc, "%");
    }
    
    private void load(DatabaseContext dc, String tableNamePattern) {
        schema = dc.getSchema();
        LogWriter.debug(logger, "schema", schema);
        Connection conn = ConnectionFactory.getConnection(dc);
        try {
            initTables(conn, dc.getUserName(), tableNamePattern);
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        loaded = true;
    }

    private void initTables(Connection conn, String userName, String tableNamePattern) {
        String[] types = {"TABLE", "VIEW"};
        
        try {
            // 检查数据库是否区分catalog
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet databases = dbmd.getCatalogs();

            boolean hasCatalogs = false;

            // TODO: 需要根据各数据库的情况决定加载方式（开放接口）
            // All Databases from Connection
            while (databases.next()) {
                hasCatalogs = true;
                LogWriter.debug(logger, "catalog", databases.getString(1));
            }
            String catalogName = hasCatalogs ? userName : null;

            final DatabaseMetaData metadata = conn.getMetaData();
            // creating tables takes a LONG time (based on JProbe analysis).
            // it's actually DatabaseMetaData.getIndexInfo() that's the pig.

            ResultSet rs = metadata.getTables(catalogName, schema, tableNamePattern, types);

            // "prime the pump" so if there's a database problem we'll probably
            // see it now and not in a secondary thread
            while (rs.next()) {
                // some databases(MySQL) return more than we wanted
                if (rs.getString("TABLE_TYPE").equals("TABLE")) {
                    String _schema = rs.getString("TABLE_SCHEM");  // MySql 的此字段返回空, 自动填写上用户名作为
                    if (_schema == null && this.schema != null) {
                        _schema = this.schema;
                    }
                    String tableName = rs.getString("TABLE_NAME");
                    Table table = new Table(_schema, tableName.toUpperCase());

                    // 加载所有的列
                    initColumns(table, catalogName, _schema, tableName, metadata);

                    String fullName = _schema == null ? tableName.toUpperCase() : (_schema.toUpperCase() + "."
                            + tableName.toUpperCase());
                    LogWriter.info(logger, "Load the table fullName", fullName);
                    tables.put(fullName, table);
                }
            }
        } catch (SQLException e) {
            throw new SystemException("装载数据库元信息时发生异常!", e);
        }
    }

    private void initColumns(Table table, String catalogName, String schema, String name,
            DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        synchronized (Table.class) {
            try {
                rs = meta.getColumns(catalogName, schema, name, "%");
                List columns = new ArrayList();
                List pks = new ArrayList();
                Set pkNames = initPrimaryKeys(meta, catalogName, schema, name);
                while (rs.next()) {
                    Column col = getColumn(table, rs);
                    col.setSqlType(rs.getInt("DATA_TYPE"));
                    columns.add(col);
                    // 设置主键
                    if (pkNames.contains(col.getName())) {
                        col.setPrimaryKey(true);
                        pks.add(col);
                    }
                }

                table.setColumns((Column[]) columns.toArray(new Column[columns.size()]));
                table.setPks((Column[]) pks.toArray(new Column[pks.size()]));
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }

    }

    private Column getColumn(Table table, ResultSet rs) throws SQLException {
        String columnName = rs.getString("COLUMN_NAME");

        if (columnName == null) {
            throw new SystemException("列名为空!");
        }

        Column column = new Column(table, columnName.toUpperCase());
        column.setType(rs.getString("TYPE_NAME"));
        column.setWidth(rs.getInt("COLUMN_SIZE"));
        column.setDec(rs.getInt("DECIMAL_DIGITS"));
        boolean nullable = (rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
        column.setNullable(nullable);
        // String defaultValue = rs.getString("COLUMN_DEF");
        // if (null != defaultValue) {
        // column.setDefaultValue(rs.getString("COLUMN_DEF"));
        // }
        return column;
    }

    private Set initPrimaryKeys(DatabaseMetaData meta, String catalogName, String schema,
            String name) throws SQLException {
        Set pkNames = new HashSet(3);

        ResultSet rs = null;

        try {
            rs = meta.getPrimaryKeys(catalogName, schema, name);

            while (rs.next()) {
                pkNames.add(rs.getString("COLUMN_NAME").toUpperCase());
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return pkNames;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
