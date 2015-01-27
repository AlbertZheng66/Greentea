package com.xt.comm.privilege;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BaseException;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public interface PrivilegeController
{
    /**
     * 将权限转换器存放在Servlet上下文的属性中的名称
     */
    public static final String PRIVILEGE_CONTROLLER_IN_SERVLET_CONTEXT
        = "PRIVILEGE_CONTROLLER_IN_SERVLET_CONTEXT";

    /**
     * 权限控制器的促使化部分
     * @param initValue 初始化数据（保持在web.xml中的数据）
     */
    public void init (Map initValue);

    /**
    * @deprecated 由下面的方法代替
     * 是否允许此用户访问此权限,如果未实现此方法，请返回真（true）
     * @param user 发出请求的用户
     * @param privilege 经过权限转换后的权限（将请求连接转换成唯一的权限编号）
     * @return 如果允许，返回真，否则返回假
     */
    public boolean permit (User user, Privilege privilege);

    /**
    * @deprecated 由下面的方法代替
     * 是否允许此用户访问此权限（此方法用来代替上一同名代两个参数方法）,如果未实现此方法，请返回真（true）
     * @param user 发出请求的用户
     * @param privilege 经过权限转换后的权限（将请求连接转换成唯一的权限编号）
     * @param request Http请求
     * @return 如果允许，返回真，否则返回假
     */
    public boolean permit (User user, Privilege privilege, HttpServletRequest request,
                           Connection conn)
        throws BaseException;


}
