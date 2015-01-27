package com.xt.core.map;

import java.util.ArrayList;
import java.util.List;

import com.xt.core.db.meta.Table;

/**
 * <p>Title: XT框架-持久化部分－类和表映射.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class MappingClass
{
	/**
	 * 类名称
	 */
	private String name;

	/**
	 * 表名称
	 */
    private Table table;

    /**
     * 类所包含的属性列表
     */
    private List properties = new ArrayList();

    public MappingClass ()
    {
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Table getTable ()
    {
        return table;
    }

    public void setTable (Table table)
    {
        this.table = table;
    }

    public List getProperties ()
    {
        return properties;
    }

    public void setProperties (List properties)
    {
        this.properties = properties;
    }

    public void addProperty (Property property)
    {
        this.properties.add(property);
    }

}
