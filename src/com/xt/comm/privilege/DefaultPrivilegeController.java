package com.xt.comm.privilege;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BaseException;
import com.xt.core.log.LogWriter;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 默认的权限控制器</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DefaultPrivilegeController
    implements PrivilegeController
{
    public DefaultPrivilegeController ()
    {
    }

    /**
     * 权限控制器的促使化部分
     */
    public void init (Map initValue)
    {
    }

    /**
     * 是否允许此用户访问此权限
     * @param user 发出请求的用户
     * @param privilege 经过权限转换后的权限（将请求连接转换成唯一的权限编号）
     * @return 如果允许，返回真，否则返回假
     */
    public boolean permit (User user, Privilege privilege)
    {
        if (privilege == null)
        {
            return false;
        }

        //如果权限的属性是公共属性，则允许所有用户访问
        if (privilege instanceof PublicPrivilege)
        {
            return true;
        }
        else if (privilege instanceof GroupPrivilege)
        {
            //等待处理
            return true;
        }
        LogWriter.info("DefaultPrivilegeController permit privilege.getId()=" + privilege.getId());

        //对用户进行认证（如果用户已经成功登录，判断其是否有权访问）。
        if (user != null)
        {
            return authorize(user, privilege);
        }
        return false;
    }

    /**
     * 对用户进行认证。
     * @param user 存放在Session中的用户。
     * @param p 当前请求的权限
     * @return 用户是否拥有当前请求的权限
     */
    public boolean authorize (User user, Privilege privilege)
    {
        LogWriter.debug("DefaultPrivilegeController authorize................");
        boolean isPermit = false;
        //如果用户有被拒绝访问此权限，则用户不可以访问。
        if (user.decline(privilege))
        {
            isPermit = false;
        }
        //如果用户有直接访问此权限的权利，则用户可以访问。
        else if (user.hasPrivilege(privilege))
        {
            isPermit = true;
        }
        //如果用户没有明确的拒绝或允许访问此权限，则依靠用户的角色来判断。
        else
        {
            Collection roles = user.getRoles();
            if (roles != null)
            {
                for (Iterator it = roles.iterator(); it.hasNext(); )
                {
                    Role role = (Role)it.next();
                    if (role.hasPrivilege(privilege))
                    {
                        isPermit = true;
                        break;
                    }
                }
            }
        }
        return isPermit;
    }

    public boolean permit (User user, Privilege privilege, HttpServletRequest request,
                           Connection conn)
        throws BaseException
    {
        return true;
    }
}
