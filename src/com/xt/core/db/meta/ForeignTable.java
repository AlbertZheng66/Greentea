package com.xt.core.db.meta;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 外键表。用于与一个表相关联的外键的表，适用于字典的情况。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ForeignTable
{
    /**
     * 主表
     */
    private Table primaryTable;

    /**
     * 主表的键值列
     */
    private Column primaryKey;
    
    /**
     * 主表
     */
    private Table foreinTable;

    /**
     * 外表的键值列
     */
    private Column foreignKey;

    public ForeignTable ()
    {
    }

    public Column getForeignKey ()
    {
        return foreignKey;
    }

    public Column getPrimaryKey ()
    {
        return primaryKey;
    }

    public Table getPrimaryTable ()
    {
        return primaryTable;
    }

    public void setForeignKey (Column foreignKey)
    {
        this.foreignKey = foreignKey;
    }

    public void setPrimaryKey (Column primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public void setPrimaryTable (Table primaryTable)
    {
        this.primaryTable = primaryTable;
    }

	public Table getForeinTable() {
		return foreinTable;
	}

	public void setForeinTable(Table foreinTable) {
		this.foreinTable = foreinTable;
	}
    
    
}
