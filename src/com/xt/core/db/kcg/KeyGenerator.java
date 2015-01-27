package com.xt.core.db.kcg;

import java.sql.Connection;

import com.xt.core.exception.POException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:主键产生器,用于自动生成表的主键. </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface KeyGenerator
{
    public long generate(String tableName, String colName, Connection conn) throws
            POException;
}
