package com.xt.core.proc.impl;

import java.lang.reflect.Method;

import com.xt.core.proc.Processor;
import com.xt.core.session.Session;
import com.xt.proxy.Context;

public class SessionProcessor implements Processor {

    private Session session;

    public SessionProcessor() {
    }
    

    public void onCreate(Class serviceClass, Session session, Context context) {
        this.session = session;
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        SessionAware sa = (SessionAware) service;
        // 如果使用了其他Session形式（比如：Cluster的情况，本Session形式将失效）
        if (sa.getSession() == null) {
            sa.setSession(session);
        }
        return params;
    }

    public void onAfter(Object service, Object ret) {
    }

    public void onFinally() {
        session = null;
    }

    public void onThrowable(Throwable t) {
    }
    
    
    public void onFinish() {
        // do nothing....
    }
}
