package com.xt.comm.privilege;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class PrivilegeFactory {

    private PrivilegeFactory()
    {
    }

    /**
     * 已经创建的所有的权限
     */
    private static Map privileges = new HashMap ();

    /**
     * 首先判断权限是否存在，如果存在返回已经存在的权限；否则，创建一个新的权限，并将其加入到权限表
     * 中。
     * @param privilegeId 权限编号
     * @return 与权限编号对应的权限
     */
    public static Privilege newInstance (String privilegeId)
    {
        if (!privileges.containsKey(privilegeId))
        {
            Privilege privilege = new Privilege(privilegeId);
            privileges.put(privilegeId, privilege);
            return privilege;
        }
        return (Privilege)privileges.get(privilegeId);
    }
}
