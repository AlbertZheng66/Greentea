package com.xt.core.test.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import junit.framework.TestCase;

import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.log.LogWriter;

/**
 * <p>Title: GreeTea 框架。</p>
* <p>Description: 如果单元测试类需要进行数据库连接需要继承此类。在setUp ()方法中，默认的建立一条<br>
*                 到测试数据库的连接，在tearDown ()方法中释放这条连接。另外，还提供了一些方便<br>
*                 向数据库表中插入测试数据的方法。
 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DBTestCase
    extends TestCase
{

    /**
     * 测试的物理表的名称
     */
    protected String tableName = null;

    /**
     * 数据库连接，在setUp()中建立连接，在tearDown()中关闭连接。
     */
    protected Connection conn;
    
    protected DatabaseContext theDatabaseContext;

    /* 此类函数是否处于调试状态 */
    private boolean debugOn = false;

    public DBTestCase ()
    {
        super();
    }

    public DBTestCase (String name)
    {
        super(name);
    }

    protected void setUp ()
        throws Exception
    {
        super.setUp();
        if (conn != null)
        {
            //建立数据库连接
            DatabaseContext dc = new DatabaseContext();
		    dc.setType(DatabaseContext.JDBC);
		    dc.setDriverClass("oracle.jdbc.driver.OracleDriver");
		    dc.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:usboss");
		    dc.setUserName("usboss");
		    dc.setPassword("usboss");
            conn = ConnectionFactory.getConnection(dc);
        }
    }

    /**
     * 重载TestCase的方法,打印希望得到和实际的字符串
     * @param expected String
     * @param actual String
     */
    static public void assertEquals(String expected, String actual) {
        LogWriter.debug("expected=" + expected);
        LogWriter.debug("actual  =" + actual);
        TestCase.assertEquals(expected, actual);
    }

    /**
     * 清空默认表的所有的数据
     */
    protected void clearTable ()
    {
        clearTable(this.tableName);
    }

    /**
     * 清空指定表的所有的数据
     * @param tableName 表格的名称
     */
    protected void clearTable (String tableName)
    {
        String sql = "DELETE FROM " + tableName;
        try
        {
            conn.createStatement().executeUpdate(sql);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * 插入数据，被插入的表由tableName指定。
     * @param values 指定的表格的所有字段的值，以字符串形式表示
     */
    protected void insertData (String[] values)
    {
        String sql = "INSERT INTO " + tableName + "(" + getFields(tableName) + ") " + " VALUES("
                     + getQuestionMarks(values.length) + ")";

        LogWriter.debug("BPOTestCase sql=" + sql, debugOn);
        String[] fieldTypes = getFieldTypes(tableName);
        try
        {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < values.length; i++)
            {
                createPreparedStatement(ps, i + 1, fieldTypes[i], values[i]);
            }
            int rows = ps.executeUpdate();
            if (rows < 1)
            {
                this.fail();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * 返回指定表的所有字段形成的字符串
     * @param tableName 表名称
     * @return 指定表的所有字段形成的字符串
     */
    private String getFields (String tableName)
    {
        //表的所有字段形成的字符串（以逗号分隔）
        StringBuffer fields = new StringBuffer();

        //提取表格的数据源信息
        String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
        try
        {
            Statement stmt = conn.createStatement();
            LogWriter.debug("BPOTestCase getFields sql=" + sql, debugOn);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            LogWriter.debug("BPOTestCase getFields columnCount=" + columnCount, debugOn);
            for (int i = 0; i < columnCount; i++)
            {
                LogWriter.debug("BPOTestCase getFields metaData.getColumnName(i + 1)="
                                + metaData.getColumnName(i + 1), debugOn);
                fields.append(metaData.getColumnName(i + 1)).append(",");
            }

            //去除最后的逗号
            fields.delete(fields.length() - 1, fields.length());
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            this.fail();
        }
        LogWriter.debug("BPOTestCase getFields fields=" + fields.toString(), debugOn);
        return fields.toString();
    }

    /**
     * 返回指定数量的问号，问号间以逗号分隔
     * @param count 返回的问号的数量
     * @return 问号字符串
     */
    private String getQuestionMarks (int count)
    {
        StringBuffer questionMarks = new StringBuffer();
        for (int i = 0; i < count; i++)
        {
            questionMarks.append("?,");
        }

        //去除最后的逗号
        questionMarks.delete(questionMarks.length() - 1, questionMarks.length());
        return questionMarks.toString();
    }

    /**
     * 返回指定表的所有字段形成的字符串
     * @param tableName 表名称
     * @return 指定表的所有字段形成的字符串
     */
    private String[] getFieldTypes (String tableName)
    {
        //表的所有字段形成的字符串（以逗号分隔）
        String[] fieldTypes = null;

        //提取表格的数据源信息
        String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            fieldTypes = new String[columnCount];
            for (int i = 0; i < columnCount; i++)
            {
                LogWriter.debug("BPOTestCase getFieldTypes metaData.getColumnTypeName(i + 1)="
                                + metaData.getColumnTypeName(i + 1), debugOn);
                fieldTypes[i] = metaData.getColumnTypeName(i + 1);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            this.fail();
        }
        return fieldTypes;
    }

    /**
     * 数据库插入语句。
     * @param insertStmt String
     */
    protected void insertData (String insertStmt)
    {
        try
        {
            conn.createStatement().executeUpdate(insertStmt);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * 单元测试结束时，系统将自动调用此方法，清理用户的占有的系统资源。
     * 重载此方法时一定要调用此语句，否则数据库链接将不能正常关闭。
     * @throws Exception
     */
    protected void tearDown ()
        throws Exception
    {
        if (conn != null && !conn.isClosed())
        {
            conn.close();
        }
        super.tearDown();
    }

    /**
     * 生成可以上下滚动，只读的PreparedStatement实例，然后为PreparedStatement的指定位置（index）<br>
     * 根据指定类型设置指定的值。不同得数据库会产生不同的字段类型的名称.
     * @param ps 生成的PreparedStatement实例
     * @param index 指示SQL查询语句中参数的位置
     * @param type 参数的类型
     * @param value 设置的参数得值值
     * @throws SQLException
     */
    private void createPreparedStatement (PreparedStatement ps, int index, String type,
                                          String value)
        throws SQLException
    {
        if (value == null)
        {
            ps.setNull(index, Types.CHAR);
        }
        else if (type.equalsIgnoreCase("CHAR") || type.equalsIgnoreCase("varchar")
                 || type.equalsIgnoreCase("text"))
        {
            ps.setString(index, (String)value);
        }
        else if (type.equalsIgnoreCase("REAL"))
        {
            ps.setDouble(index, Double.parseDouble(value));
        }
        else if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("smallint")
                 || type.equalsIgnoreCase("TINYINT"))
        {
            ps.setInt(index, Integer.parseInt(value));
        }
        else if (type.equalsIgnoreCase("NUMERIC") || type.equalsIgnoreCase("double precision"))
        {
            ps.setDouble(index, Double.parseDouble(value));
        }
        else if (type.equalsIgnoreCase(""))
        {
            ps.setBoolean(index, Boolean.valueOf(value).booleanValue());
        }
        else if (type.equalsIgnoreCase("FLOAT"))
        {
            ps.setFloat(index, Float.parseFloat(value));
        }
        else if (type.equalsIgnoreCase("varbinary") || type.equalsIgnoreCase("datetime"))
        {
            ps.setTimestamp(index, new Timestamp(Long.parseLong(value)));
        }
        else
        {
            ps.setObject(index, value);
        }
    }

    protected String getTableName ()
    {
        return this.tableName;
    }

    /**
     * 返回数据库名称,如果测试程序想修改连接的数据库，可以重载此方法。数据库名称必须是配置文件中
     * 存在的名称，否则将产生异常。
     * @return 数据库名称
     */
    protected String getDBName ()
    {
//        if (tableName != null)
//        {
            return "odbc/assets";
//        }
//        return null;
    }

}
