package com.xt.comm.privilege;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: </p>
 * <p>Description: 此接口用于将Http请求链接转换成相应的权限。</p>
 * <p>Copyright: Copyright (c) 2003年5月</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */


/*
 *
 */
public interface PrivilegeTranslation
{
    /**
     * 初始化请求转换器
     */
    public void init (Map initValue);

    /**
     * 根据Http请求得到此请求对应的权限
     * @param request Http 请求
     * @return Http请求对应的权限
     */
    public Privilege getPrivilege (HttpServletRequest request);
}
