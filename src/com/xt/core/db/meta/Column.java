package com.xt.core.db.meta;


/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 此类表示了数据库表中的一列包含的源数据.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class Column
{
    private final Table table;
    /**
     * 字段名称
     */
    private final String name;

    /**
     * 字段类型,目前采用数据库类型(数据库类型间的差异较大,所以这种方式是不稳定的)
     */
    private String type;
    
    /**
     * java.sql.Types中定义的类型
     */
    private int sqlType;

    /**
     * 列的宽度(the designated column's number of digits to right of the decimal point)
     */
    private int width;

    /**
     * 列的精度
     */
    private int dec;

    /**
     * 是否运行为空，允许为真，否则为假
     */
    private boolean nullable;

    /**
     * 缺省值
     */
    private Object defaultValue;

    /**
     * 此字段是否是主键
     */
    private boolean primaryKey = false;

    /**
     * 此列对应的字典表
     */
    private ForeignTable foreignTable;

    /**
     * 字段显示的标题
     */
    private String title;

    public Column (Table table, String name)
    {
    	this.table = table;
    	this.name  = name;
    }

    public int getDec ()
    {
        return dec;
    }

    public Object getDefaultValue ()
    {
        return defaultValue;
    }

    public String getName ()
    {
        return name;
    }

    public boolean isNullable ()
    {
        return nullable;
    }

    public boolean isPrimaryKey ()
    {
        return primaryKey;
    }

    public int getWidth ()
    {
        return width;
    }

    public void setWidth (int width)
    {
        this.width = width;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public void setPrimaryKey (boolean primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public void setNullable (boolean nullable)
    {
        this.nullable = nullable;
    }

    public void setDefaultValue (String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public void setDec (int dec)
    {
        this.dec = dec;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    
    public ForeignTable getForeignTable() {
		return foreignTable;
	}

	public void setForeignTable(ForeignTable foreignTable) {
		this.foreignTable = foreignTable;
	}

	public Table getTable() {
		return table;
	}


	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getType()
    {
        return type;
    }

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
	
	
}
