package com.xt.core.proc.impl;

import java.lang.reflect.Method;

import com.xt.core.proc.Processor;
import com.xt.core.session.Session;
import com.xt.proxy.Context;

public class GeneralProcessor implements Processor {

    private Processor[] processors;

    public GeneralProcessor() {
    }

    public void onCreate(Class serviceClass, Session session, Context context) {
        if (processors == null) {
            return;
        }
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            procIter.onCreate(serviceClass, session, context);
        }
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        if (processors == null) {
            return params;
        }
        Object[] _params = params;
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            _params = procIter.onBefore(service, method, _params);
        }
        return _params;

    }

    public void onAfter(Object service, Object ret) {
        if (processors == null) {
            return;
        }
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            procIter.onAfter(service, ret);
        }

    }

    public void onFinally() {
        if (processors == null) {
            return;
        }
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            procIter.onFinally();
        }

    }

    public void onThrowable(Throwable t) {
        if (processors == null) {
            return;
        }
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            procIter.onThrowable(t);
        }
    }

    /**
     * 所有处理都已经完成。
     */
    public void onFinish() {
        if (processors == null) {
            return;
        }
        for (int i = 0; i < processors.length; i++) {
            Processor procIter = processors[i];
            procIter.onFinish();
        }
    }

    public void setProcessors(Processor[] processors) {
        this.processors = processors;
    }
}
