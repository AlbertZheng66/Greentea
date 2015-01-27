package com.xt.core.db.meta;

/**
 * 这个类包含了表的元信息.
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-2
 */
public class Table
{
	/**
	 * 如果schema为空,则取默认的schema
	 */
    private final String schema;
    
    /**
     * 表的名称
     */
    private final String name;

    /**
     * 表中包含的字段
     */
    private Column[] columns;

    /**
     * 表的主键
     */
    private Column[] pks;

    public Table(String schema, String name)
    {
    	this.schema = schema;
    	this.name   = name;
    }


    /**
     * 根据列名称找到其对应的字段
     * @param columnName String
     * @return Column
     */
    public Column find(String columnName)
    {
        Column col = null;
        if (columns != null && columnName != null)
        {
            for (int i = 0; i < columns.length; i++)
            {
                //比较名称
                if (columnName.equals(columns[i].getName()))
                {
                    col = columns[i];
                    break;
                }
            }
        }
        return col;
    }

    public Column[] getColumns()
    {
        return columns;
    }

    public String getName()
    {
        return name;
    }

    public Column[] getPks()
    {
        return pks;
    }

    public void setPks(Column[] pks)
    {
        this.pks = pks;
    }

    public void setColumns(Column[] columns)
    {
        this.columns = columns;
    }

	public String getSchema() {
		return schema;
	}
    
}
