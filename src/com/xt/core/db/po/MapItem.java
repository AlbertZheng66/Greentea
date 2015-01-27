package com.xt.core.db.po;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class MapItem implements ResultSetItem
{
	
	
    public MapItem()
    {
    }


	public Object createObject(ResultSet rs) throws SQLException {
		Map<String, Object> row = new HashMap<String, Object>();
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			String columnName = rs.getMetaData().getColumnName(i);
			row.put(columnName, rs.getObject(columnName));
		}
		return row;
	}

}
