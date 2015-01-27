package com.xt.core.db.pm;

import java.sql.Connection;

public interface PersistenceDialect {
	/**
	 * 返回数据库用于完成获取Sequence的SQL语句
	 * @param seqName
	 * @return
	 */
    public String getSequenceSql (Connection conn, String sequenceName);
}
