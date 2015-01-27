package com.xt.core.proc.impl;

import com.xt.core.service.LocalMethod;
import com.xt.core.session.Session;

/**
 * 当服务需要从 session 中读取数据或者在Session中存入数据时，可实现此接口。
 * 框架将自动注入合适的session，Session的实现与使用的代理类型相关。
 * @author albert
 *
 */
public interface SessionAware {

    @LocalMethod
    public void setSession(Session session);

    @LocalMethod
    public Session getSession();
}
