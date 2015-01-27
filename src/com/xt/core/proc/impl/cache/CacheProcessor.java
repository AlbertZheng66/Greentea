package com.xt.core.proc.impl.cache;

import com.xt.core.proc.AbstractProcessor;
import java.lang.reflect.Method;

/**
 *
 * @author albert
 */
public class CacheProcessor  extends AbstractProcessor {

    public CacheProcessor() {
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        if (service instanceof CacheAware) {
            ((CacheAware) service).setCacheManager(null);
        }
        return params;
    }
}
