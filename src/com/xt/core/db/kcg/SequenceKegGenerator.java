package com.xt.core.db.kcg;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: 顺序主键产生器.根据指定的表,指定的列依次产生其主键.从1开始进行增加.产生的方式是:初始化时
 * 读取指定表的指定列的最大值,存放在缓冲器中,得到的主键将最大数加一.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class SequenceKegGenerator
    implements KeyGenerator
{
    /**
     * 表的主键的缓存器,保存在内存中.主键是表名+"."+列名称,键值是此表的此字段的最大值.
     */
    private Map keys = new HashMap();

    public SequenceKegGenerator ()
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
    public synchronized long generate (String tableName, String colName, Connection conn)
    {
        long key = 0;
        Object obj = keys.get(tableName + "." + colName);

        //如果主键尚未存在,产生主键
        if (obj == null)
        {
            obj = initialize(tableName, colName, conn);
        }

        //将主键加1
        key = ((Long)obj).longValue() + 1;
        keys.put(tableName + "." + colName, new Long(key));

        return key;
    }

    private Long initialize (String tableName, String colName, Connection conn)
    {
        Long key = null;
        String sql = "SELECT MAX(" + colName + ") " + tableName;
        try
        {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next())
            {
                key = new Long(rs.getLong(1));
            }
        }
        catch (SQLException ex)
        {
            //尚未处理
        }
        return key;
    }

}
