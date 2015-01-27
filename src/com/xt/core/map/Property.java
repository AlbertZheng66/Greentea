package com.xt.core.map;

import com.xt.core.db.meta.Column;
import com.xt.core.utils.ClassHelper;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class Property
{

    private String name;

    private Class type;

    private Column column;

    public Property ()
    {
    }

    public Column getColumn ()
    {
        return column;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public void setColumn (Column column)
    {
        this.column = column;
    }

    public Class getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        if ("int".equals(type)) {
            this.type = int.class;
        } else if ("short".equals(type)) {
            this.type = short.class;
        } else if ("byte".equals(type)) {
            this.type = byte.class;
        } else if ("char".equals(type)) {
            this.type = char.class;
        } else if ("double".equals(type)) {
            this.type = double.class;
        } else  if ("float".equals(type)) {
            this.type = float.class;
        } else if ("long".equals(type)) {
            this.type = long.class;
        } else if ("String".equals(type)) {
            this.type = String.class;
        } else if ("boolean".equals(type)) {
            this.type = boolean.class;
        } else  if (type != null) {
            this.type = ClassHelper.getClass(type);
        }
    }

}
