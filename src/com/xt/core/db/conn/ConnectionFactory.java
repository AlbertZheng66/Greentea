package com.xt.core.db.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.POException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import java.sql.ResultSet;
import java.util.Map;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:这个类的作用是用来建立数据库连接。 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ConnectionFactory {
	
	private final static Logger logger = Logger.getLogger(ConnectionFactory.class); 
	
    /**
     * 系统中所有可用的数据源的集合。主键是数据源的名称，对应的值是数据源的实例。
     */
    private static Map dataSources = new HashMap();

    public ConnectionFactory() {
    }

    public static Connection getDefaultConnection() throws SystemException {
        DatabaseContextManager manager = DatabaseContextManager.getInstance();
        DatabaseContext dbContext = manager.getDefaultDatabaseContext();
        return getConnection(dbContext);
    }


    public static Connection getConnection(String dbName) throws
            SystemException {
        DatabaseContextManager manager = DatabaseContextManager.getInstance();
        DatabaseContext dbContext = manager.getDatabaseContext(dbName);
        return getConnection(dbContext);
    }

    public static Connection getConnection(DatabaseContext dbContext) throws
            SystemException {
        Connection conn = null;
        if (DatabaseContext.ODBC.equals(dbContext.getType())) {
            conn = getOdbcConnection(dbContext);
        } else if (DatabaseContext.JDBC.equals(dbContext.getType())) {
            conn = getJdbcConnection(dbContext);
        } else if (DatabaseContext.DATA_SOURCE.equals(dbContext.getType())) {
            conn = getDsConnection(dbContext);
        } else {
        	throw new SystemException(String.format("未知的数据库连接类型[%s]!", dbContext.getType()));
        }
        return conn;
    }

    /**
     * 建立数据库连接
     * @return 分配一条数据库连接
     * @throws POException
     */
    private static Connection getOdbcConnection(DatabaseContext dbContext) throws
            SystemException {
        Connection conn = null; //数据库连接
        checkParameter(dbContext.getDatabaseName());
        try {
            //应该使用配置文件方式处理
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conn = DriverManager.getConnection("jdbc:odbc:" +
                                               dbContext.getDatabaseName(),
                                               dbContext.getUserName(),
                                               dbContext.getPassword());
        } catch (SQLException ex) {
            throw new SystemException("数据库连接出现异常。", ex);
        } catch (ClassNotFoundException ex) {
            throw new SystemException("未发现数据库连接类。", ex);
        }
        return conn;
    }

    /**
     * 建立数据库连接
     * @return 分配一条数据库连接
     * @throws SystemException
     */
    private static Connection getDsConnection(DatabaseContext dbContext) throws
            SystemException {
        Connection conn = null; //数据库连接
        //数据源名称
        String dsName = dbContext.getDatabaseName();
        checkParameter(dsName);
        try {
            if (dbContext.getPrefix() != null) {
                dsName = dbContext.getPrefix() + dsName;
            }
            //根据数据源的名称得到数据库链接。
            conn = getDataSource(dsName).getConnection();
        } catch (SQLException ex) {
            throw new SystemException(String.format("获取数据源[%s]时出现异常。", dsName), ex);
        }
        return conn;
    }

    private static synchronized DataSource getDataSource(String sourceName) throws
            SystemException {
        LogWriter.debug(logger, "getDataSource sourceName=", sourceName);
        
        DataSource ds = null;
        Object obj = dataSources.get(sourceName);
        if (obj == null) {
            try {
                Context ctx = new InitialContext();
                ds = (DataSource) ctx.lookup(sourceName);
            } catch (NamingException ex) {
                throw new SystemException(String.format("获取数据源[%s]时出现异常。", sourceName), ex);
            }
        } else {
            ds = (DataSource) obj;
        }
        return ds;
    }

    /**
     * 建立数据库连接
     * @return 分配一条数据库连接
     * @throws SystemException
     */
    private static Connection getJdbcConnection(DatabaseContext dbContext) throws
            SystemException {
        Connection conn = null; //数据库连接
        checkParameter(dbContext.getUrl());
        try {
            Class.forName(dbContext.getDriverClass()).newInstance();
            //根据数据源的名称得到数据库链接。
            conn = DriverManager.getConnection(dbContext.getUrl(),
                                               dbContext.getUserName(),
                                               dbContext.getPassword());
            conn.setAutoCommit(false);
        } catch (Exception ex) {
            throw new SystemException(String.format("建立数据库[%s]连接时出现异常。", dbContext.getId()), ex);
        } 
        return conn;
    }
    
    private static void checkParameter (String dbString) {
    	if (StringUtils.isEmpty(dbString)) {
        	throw new SystemException("数据库名称或者数据库连接未定义!");
        }
    }

    public static void rollBack(Connection conn) throws SystemException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            throw new SystemException("数据库回滚时出现异常。", ex);
        }
    }


    /**
     * 关闭数据库，为了保证数据的安全，在关闭数据库之前进行了一次回滚操作，
     * 因此需要先提交然后再关闭数据库。
     * @param conn 数据库连接
     * @throws SystemException 将关闭数据库产生的SQLException转换为系统异常。
     */
    public static void closeConnection(Connection conn) throws SystemException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.close();
            }
        } catch (SQLException ex) {
            throw new SystemException("关闭数据库时出现异常。", ex);
        }
    }
    
    /**
     * 测试数据库连接是否正确。如果连接为空，或者未连接上，则返回假，否则返回真。
     * @param conn 数据库连接
     * @param sql  用于检查数据库连接的SQL
     * @throws SystemException 将关闭数据库产生的SQLException转换为系统异常。
     */
    public static boolean testConnection(Connection conn, String sql) {
        if (conn == null) {
        	return false;
        }
    	try {
    		// 通过数据库产品名进行测试
    		conn.getMetaData().getDatabaseProductName();
//    		String sql = "";
    		ResultSet rs = conn.createStatement().executeQuery(sql);
            return true;
        } catch (Throwable t) {
        	LogWriter.warn(logger, "尝试和数据库建立连接失败！", t);
        	return false;
        }
    }
}
