package com.xt.core.proc.impl;

import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;

/**
 * 注入处理其用于自动注入服务只用，当一个服务需要引起其他服务时，往往需要
 * @author albert
 */
public class InjectorProcessorProcessor implements ProcessorFactory {
    
    // 所有处理都复用此实例
    private final InjectorProcessor instance = new InjectorProcessor();

    public void onInit() {
    }

    public synchronized Processor createProcessor(Class serviceClass) {
        if (Injectable.class.isAssignableFrom(serviceClass)) {
            return instance;
        }
        return null;
    }

    public void onDestroy() {
    }
}
