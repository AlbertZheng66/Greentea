package com.xt.test.ct;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:代码信息表。 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class CodeInfo
{
    /**
     * 代码表的版本号
     */
    public String version;

    /**
     * 代码表对应的物理表的名称
     */
    public String tableName;

    /**
     * 代码表是否需要缓存
     */
    public boolean cached;

    public CodeInfo()
    {
    }

    /**
     * 返回版本号（标识当前的版本信息）
     * @return 版本号
     */
    public String getVersion () {
        return version;
    }

    public String getTableName() {
        return this.tableName;
    }

    /**
     * 代码表是否进行缓存。
     * @return 如果缓存代码表，则返回真，否则返回假。
     */
    public boolean isCached () {
        return this.cached;
    }

}
