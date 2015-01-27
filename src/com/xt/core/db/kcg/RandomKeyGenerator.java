package com.xt.core.db.kcg;

import java.sql.Connection;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: 随机主键产生器,用于产生随机的主键用.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class RandomKeyGenerator implements KeyGenerator
{
    /**
     * 产生的主键的宽度.
     */
    private int width = 20;

    public RandomKeyGenerator()
    {
    }

    /**
     * generate
     *
     * @param tableName String
     * @param colName String
     * @param conn Connection
     * @return long
     */
    public long generate (String tableName, String colName, Connection conn)
    {
        return 0L;
    }
}
