package com.xt.core.proc.impl;

import com.xt.core.proc.AbstractProcessor;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import com.xt.core.session.Session;
import com.xt.proxy.Context;

public class JPAProcessor extends AbstractProcessor {

    private final EntityManager entityMananger;

    public JPAProcessor(EntityManager entityMananger) {
        this.entityMananger = entityMananger;
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        if (service instanceof JPAAware) {
            entityMananger.getTransaction().begin();
            ((JPAAware) service).setEntityManager(entityMananger);
        }
        return params;
    }

    @Override
    public void onAfter(Object service, Object ret) {
        entityMananger.getTransaction().commit();
    }

    @Override
    public void onThrowable(Throwable t) {
        entityMananger.getTransaction().rollback();
    }
}
