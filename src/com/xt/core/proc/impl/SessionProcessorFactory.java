package com.xt.core.proc.impl;

import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;

public class SessionProcessorFactory implements ProcessorFactory {

    public void onInit() {
    }

    public synchronized Processor createProcessor(Class serviceClass) {
        if (SessionAware.class.isAssignableFrom(serviceClass)) {
            Processor processor = new SessionProcessor();
            return processor;
        }
        return null;
    }

    public void onDestroy() {
    }
}
