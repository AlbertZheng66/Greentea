package com.xt.comm.privilege;

import java.util.Collection;
import java.util.Iterator;

import com.xt.core.log.LogWriter;

/**
 * <p>Title: </p>
 * <p>Description:缺省的权限转换器 </p>
 * <p>Copyright: Copyright (c) 2003年5月</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DefaultUser
    implements User
{
    private String name;

    /**
     * 用户管理的组（单位）
     */
    private Collection groups;

    /**
     * @link aggregation
     * @directed
     * @clientCardinality 1*/
    /*# Role roles; */
    /**
     * 用户拥有的角色
     */
    private Collection roles;

    /**
     * 用户的唯一标识字符串。
     */
    String id = null;

    /**
     * @directed
     * @link aggregation
     * @label 直接授予或拒绝
     */
    /*# Privilege declinedPrivileges; */
    /**
     * 直接拒绝的权限
     */

    private Collection declinedPrivileges;

    /**
     * 直接授予用户的权限
     */
    private Collection ownedPrivileges;

    public DefaultUser ()
    {
    }

    public void addPrivilege (Privilege p)
    {
        LogWriter.debug("DefaultUser addPrivilege..............................");
//		if (privileges == null)
//		{
//			privileges = new HashSet();
//		}
//		privileges.add(p);
    }

    public void removePrivilege (Privilege p)
    {
//		if (privileges != null && p != null && privileges.contains(p))
//		{
//			privileges.remove(p);
//		}
    }

    public void addDeclinePrivilege (Privilege p)
    {
        LogWriter.debug("DefaultUser addDeclinePrivilege..............................");
//		if (declinePrivileges == null)
//		{
//			declinePrivileges = new HashSet();
//		}
//		declinePrivileges.add(p);
    }

//        public boolean authorize (Privilege p)
//        {
//                boolean isPermit = false;
//                if (p == null)
//                {
//                        return false;
//                }
//                if (decline(p))
//                {
//                        isPermit = false;
//                }
//                else if (hasPrivilege(p))
//                {
//                        isPermit = true;
//                }
//                else
//                {
//                        Collection roles = getRoles();
//                        if (roles != null)
//                        {
//                                for (Iterator it = roles.iterator(); it.hasNext(); )
//                                {
//                                        Role role = (Role) it.next();
//					if (role.hasPrivilege(p))
//					{
//						isPermit = true;
//						break;
//					}
//                                }
//                        }
//                }
//                return isPermit;
//        }

    public String toString ()
    {
        StringBuffer sb = new StringBuffer("DefaultUser:[id=");
        sb.append(this.id).append(";").append("name=").append(this.name).append("]");
        return sb.toString();
    }

    public void removeDeclinePrivilege (Privilege p)
    {
//		if (declinePrivileges != null && p != null
//			&& declinePrivileges.contains(p))
//		{
//			declinePrivileges.remove(p);
//		}
    }

    public boolean decline (Privilege p)
    {
//		if (p != null && declinePrivileges != null
//			&& contains(declinePrivileges, p))
//		{
//			return true;
//		}
        return false;
    }

    private boolean contains (Collection col, Privilege p)
    {
        LogWriter.debug("DefaultUser.contains.col=" + col);
        for (Iterator it = col.iterator(); it.hasNext(); )
        {
            if (p.equals((Privilege)it.next()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasPrivilege (Privilege p)
    {
        boolean isPermit = false;
//		if (p != null && privileges != null && contains(privileges, p))
//		{
//			isPermit = true;
//		}

        return isPermit;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public void addRole (Role role)
    {
//		if (this.roles == null)
//		{
//			roles = new ArrayList();
//		}
//		roles.add(role);
    }

    public String getName ()
    {
        return name;
    }

    /**
     * @label 属于
     * @clientCardinality 1
     * @supplierCardinality 1*/
    private Group group;

    /**
     * 得到用户所属的用户组
     * @return 用户所属的用户组
     */
    public Group getGroup ()
    {
        return null;
    }

//    /**
//     * 用户是否拒绝此权限；拒绝返回真，否则返回假。
//     * @param p 传给用户判断的权限
//     * @return 传给用户判断的权限为空，返回假；拒绝此权限返回真，否则返回假。
//     */
//    public boolean decline (Privilege p)
//    {
//        if (p == null || declinedPrivileges == null)
//        {
//            return false;
//        }
//        for (Iterator iter = declinedPrivileges.iterator(); iter.hasNext(); )
//        {
//            Privilege item = (Privilege) iter.next();
//            if (p.equals(item))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

//    /**
//     * 用户是否拥有此权限。拥有返回真，否则返回假。
//     * @param p 传给用户判断的权限
//     * @return 传给用户判断的权限为空，返回假；拥有此权限返回真，否则返回假。
//     */
//    public boolean hasPrivilege (Privilege p)
//    {
//        if (p == null || ownedPrivileges == null)
//        {
//            return false;
//        }
//        for (Iterator iter = ownedPrivileges.iterator(); iter.hasNext(); )
//        {
//            Privilege item = (Privilege) iter.next();
//            if (p.equals(item))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 用户对权限进行认证。
     * @param p 权限。
     * @return 如果用户拥护该权限，返回真，否则返回假
     */
    public boolean authorize (Privilege p)
    {
        if (p == null || roles == null)
        {
            return false;
        }
        //检验用户的每一个角色
        for (Iterator iter = roles.iterator(); iter.hasNext(); )
        {
            Role role = (Role)iter.next();
            if (role.hasPrivilege(p))
            {
                return true;
            }
        }
        return false;
    }

    public Collection getGroups ()
    {
        return groups;
    }

    public String getId ()
    {
        return id;
    }

    public Collection getRoles ()
    {
        return roles;
    }

    public void setRoles (Collection roles)
    {
        this.roles = roles;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public void setGroups (Collection groups)
    {
        this.groups = groups;
    }

}
