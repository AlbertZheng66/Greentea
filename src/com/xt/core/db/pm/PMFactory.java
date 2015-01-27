package com.xt.core.db.pm;

import java.sql.Connection;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface PMFactory
{
    public final static String TRANSACTION_LEVEL = "";

    /**
     * O/R映射的类型,自定义类型
     */
    public static final String DEFAULT = "DEFAULT";

    /**
     * O/R映射的类型,采用Hibernate的方式
     */
    public static final String HIBERNATE = "HIBERNATE";

    /**
     * O/R映射的类型,采用EJB
     */
    public static final String EJB = "EJB";

    /**
     * O/R映射的类型,采用JDO
     */
    public static final String JDO = "JDO";

    public PersistenceManager createPersistenceManager(Object obj);

    /**
     * 设置是否自动提交
     * @param flag boolean
     */
    public void setAutoCommit(boolean flag);

    /**
     * 设置事务级别
     * @param flag boolean
     */
    public void setTransactionLevel(boolean flag);

    /**
     * 返回数据库连接,如果用户自己调用了数据库连接,则需要用户自己管理数据库事务.
     * @param name String 数据库连接的名称(数据库的名称),如果为空,表示请求默认的数据库连接.
     * @return Connection 如果请求的名称不存在,返回空.
     */
    public Connection getConnection (String name);

}
