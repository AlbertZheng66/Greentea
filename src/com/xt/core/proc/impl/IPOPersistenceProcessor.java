package com.xt.core.proc.impl;

import java.lang.reflect.Method;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.proc.AbstractProcessor;
import com.xt.core.service.AutoLoadable;
import com.xt.core.session.Session;
import com.xt.proxy.Context;
import com.xt.gt.ui.fsp.FspParameter;

/**
 * 持久化处理器， 在服务类里自动注入持久化管理器。
 * 
 * @author albert
 * 
 */
public class IPOPersistenceProcessor  extends AbstractProcessor {

    protected final IPOPersistenceManager persistenceManager;

    public IPOPersistenceProcessor(IPOPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public void onCreate(Class serviceClass, Session session, Context context) {
        persistenceManager.setAutoCommit(false);
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        // 要检查业务类是否定义了持久化参数
        IPOPersistenceAware persistenceService = (IPOPersistenceAware) service;

        // 已经用其他方式对这个实例进行了持久化处理（比如主从数据库）
        if (persistenceService.getPersistenceManager() != null) {
            return params;
        }
        persistenceManager.beginTransaction();
        persistenceService.setPersistenceManager(persistenceManager);

        if (service instanceof AutoLoadable) {
            FspParameter fspParameter = ((AutoLoadable) service).getFspParameter();
            if (fspParameter != null) {
                persistenceManager.setFetchSize(fspParameter.getPagination().getRowsPerPage());
                persistenceManager.setStartIndex(fspParameter.getPagination().getStartIndex());
            }
        }
        return params;
    }

    @Override
    public void onAfter(Object service, Object ret) {
        persistenceManager.commit();
    }

    @Override
    public void onFinally() {
        persistenceManager.close();
    }

    @Override
    public void onThrowable(Throwable t) {
        persistenceManager.rollback();
    }
}
