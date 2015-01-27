/* Generated by Together */

package com.xt.comm.privilege;

import java.util.Collection;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003年5月</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class GroupPrivilege
    extends Privilege
{

    /**
     * @link aggregation
     * @directed
     * @clientCardinality 1
     * @label 拥有多个用户组
     */
    /*# Group groups; */
    /**
     * 组权限拥有的多个用户组的集合.
     */
    private Collection groups;

    /**
     * 组权限是否包含指定的用户组
     * @param g 用户组
     * @return 如果包含返回真，否则返回假。
     */
    public boolean hasGroup (Group g)
    {
        if (g == null || groups == null)
        {
            return false;
        }
        return groups.contains(g);
    }
}
