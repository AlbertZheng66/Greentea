package com.xt.core.proc.impl.cache;

import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;

/**
 *
 * @author albert
 */
public class CacheProcessorFactory implements ProcessorFactory {

    private CacheManager cacheManager;

    public void onInit() {
        // 启动缓存处理器
    }

    public Processor createProcessor(Class serviceClass) {
        if (CacheManager.class.isAssignableFrom(serviceClass)) {
            return new CacheProcessor();
        }
        return null;
    }

    public void onDestroy() {
        // 关闭缓存
    }
}
