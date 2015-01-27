package com.xt.core.db.meta;


/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 数据库元信息装载器,用于装载数据库的元信息.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface Loader
{
    public Table load (String tableName)
        throws LoaderException;
}
