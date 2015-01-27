package com.xt.core.db.po;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface ResultSetItem {

    /**
     * 使用此方法将查询得到的结果集转换成使用人员自定义的表现方式
     * @param rs 查询结果集
     * @return 自定义的表现方式
     * @throws SQLException
     */
    public Object createObject (ResultSet rs)
        throws SQLException;
}
