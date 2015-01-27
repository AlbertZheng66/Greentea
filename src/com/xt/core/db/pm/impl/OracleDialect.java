package com.xt.core.db.pm.impl;

import java.sql.Connection;

import com.xt.core.db.pm.PersistenceDialect;

public class OracleDialect implements PersistenceDialect{

	public String getSequenceSql(Connection conn, String sequenceName) {
		return "SELECT " + sequenceName + ".NEXT_VAL FROM DUAL ";
	}

}
