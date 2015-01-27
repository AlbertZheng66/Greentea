package com.xt.core.db.pm;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title: </p>
 * <p>Description: 如果需要将结果集的形式转换成Item的形式，Item必须继承此接口.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Liaohe Digital</p>
 * @author 郑伟
 * @version 1.0
 */

public interface Item
{
	/**
	 * 根据当前结果集，创建一个对象。
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
    public Object createObject(ResultSet rs)
            throws SQLException;
    
    /**
     * 是否需要分页
     * @return
     */
    public boolean isPagination();
}
