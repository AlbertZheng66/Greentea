package com.xt.core.db.po;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.xt.core.db.pm.IPersistence;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: 标识接口,用于标识出类为持久化类.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface IPO extends IPersistence
{
    /**
     * 自定义设置最小的浮点数的值
     */
    public static final double MIN_DOUBLE_VALUE = 1e-50;

    /**
     * 缺省的每页最多保存的查询记录数，如果为-1,表示对查询结果不作限制
     */
    public static final int DEFAULT_MAX_ITEMS_PER_PAGE = SystemConfiguration.getInstance()
    .readInt(SystemConstants.PAGINATION_PAGE_SIZE, 200);

    /**
     * 返回PO所代表的表的表名
     * @return 表名
     */
    public String __getTableName ();
    
    /**
     * 返回对应的实体类型。
     * @return
     */
    public Class<?> __getEntityClass();

    /**
     * 返回主键数组，不能为空，且长度大于 0。
     * @return
     */
    public String[] __getPrimaryKeys ();
    
//    /**
//     * 返回从数据库装入时的原始主键，在load方法中调用，
//     * 装入原始值后就不再更改。在更改时，作为SQL语句的主键来用。
//     * 第一个值是列的名称，其后的列的对应值。
//     * @return
//     */
//    public List getOldPrimaryKeys ();

    public void __cleanAttribute ();

    /**
     * 返回存放继承PO类的子类的各个属性的赋值参数,
     * 主键为字段名称,其值为属性的值.
     */
    public Map<String, Object> __getStatus ();

    /**
     * 将查询结果装载到事例本身
     * @param rs 查询结果集
     * @throws SQLException
     */
    public void __load (ResultSet rs, Object obj)
        throws SQLException;

    public int getFetchSize();

    public int getStartIndex ();

    public void setFetchSize(int fetchSize);

    public void setStartIndex (int startIndex);
   
}
