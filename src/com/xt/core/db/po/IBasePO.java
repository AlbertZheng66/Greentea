package com.xt.core.db.po;

import java.sql.Connection;

import com.xt.core.db.pm.IPersistence;
import com.xt.core.exception.POException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public interface IBasePO extends IPersistence
{

    /**
     * 自定义设置最小的浮点数的值
     */
    public static final double MIN_DOUBLE_VALUE = 1e-50;

    /**
     * 缺省的每页最多保存的查询记录数，如果为-1,表示对查询结果不作限制
     */
    public static final int DEFAULT_MAX_ITEMS_PER_PAGE = 1000;

//    public ResultSetList findAll(Connection conn)
//        throws POException;

//    public ResultSetList findAll(Connection conn, String key)
//        throws POException;

    /**
     * 删除一条记录（进行此操作之前需要设定主键，根据主键进行删除，如果主键为空则抛出异常）
     * @param conn 数据库连接
     * @return 删除操作是否成功，如果成功返回true，否则返回false
     * @throws POException 持久对象异常
     */
    public boolean update (Connection conn)
        throws POException;

    /**
     * 删除一条记录（进行此操作之前需要设定主键，根据主键进行删除，如果主键为空则抛出异常）
     * @param conn 数据库连接
     * @return 删除操作是否成功，如果成功返回true，否则返回false
     * @throws POException 持久对象异常
     */
    public boolean delete (Connection conn)
        throws POException;

    /**
     * 插入一条记录（进行此操作之前需要设定主键，如果主键重复或者主键为空则抛出异常）
     * @param conn 数据库连接
     * @return 插入操作是否成功，如果成功返回true，否则返回false
     * @throws POException
     */
    public boolean insert (Connection conn)
        throws POException;

}
