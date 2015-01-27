package com.xt.core.proc.impl;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.exception.SystemException;
import com.xt.core.proc.AbstractProcessor;
import com.xt.core.service.IService;
import com.xt.core.session.Session;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author albert
 */
public class InjectorProcessor extends AbstractProcessor {

    public InjectorProcessor() {
    }

    public Object[] onBefore(Object orignalService, Method method, Object[] params) {
        inject(orignalService);
        return params;
    }
    
    private void inject(Object orignalService) {
        List<Field> fields = ClassHelper.getFields(orignalService.getClass(), true);
        for (Iterator<Field> it = fields.iterator(); it.hasNext();) {
            Field field = it.next();
            if (field.getAnnotation(InjectService.class) != null) {
                Object injectedService = createInjectedService(orignalService, field);
                BeanHelper.copyProperty(orignalService, field.getName(), injectedService);
            }
        }
    }

    private IService createInjectedService(Object orignalService, Field field) {
        Class injectedServiceClass = field.getType();
        if (!IService.class.isAssignableFrom(field.getType())) {
            throw new SystemException(String.format("被注入的属性[%s]的类型必须是[%s]。", field.getName(), IService.class.getName()));
        }
        IService injectedService = (IService) ClassHelper.newInstance(injectedServiceClass);

        // 注入持久化对象
        if (orignalService instanceof IPOPersistenceAware && injectedService instanceof IPOPersistenceAware) {
            IPOPersistenceManager persistenceManager = ((IPOPersistenceAware) orignalService).getPersistenceManager();
            if (persistenceManager == null) {
                // throw new SystemException("");
            }
            ((IPOPersistenceAware) injectedService).setPersistenceManager(persistenceManager);
        }

        // 注入 Session
        if (orignalService instanceof SessionAware && injectedService instanceof SessionAware) {
            Session session = ((SessionAware) orignalService).getSession();
            if (session == null) {
                // throw new SystemException("");
            }
            ((SessionAware) injectedService).setSession(session);
        }
        // 递归注入
        if (Injectable.class.isAssignableFrom(injectedService.getClass())) {
            inject(injectedService);
        }
        return injectedService;
    }
}
