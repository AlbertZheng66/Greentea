package com.xt.core.proc.impl;

import com.xt.core.log.LogWriter;
import java.util.ArrayList;
import java.util.List;

import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;
import com.xt.gt.sys.SystemConfiguration;
import org.apache.log4j.Logger;

public class GeneralProcessorFactory implements ProcessorFactory {

    ProcessorFactory[] processorFactories = null;
    private final Logger logger = Logger.getLogger(GeneralProcessorFactory.class);
    private static GeneralProcessorFactory instance = new GeneralProcessorFactory();

    private GeneralProcessorFactory() {
    }

    public static GeneralProcessorFactory getInstance() {
        return instance;
    }

    public void onInit() {
        processorFactories = (ProcessorFactory[]) SystemConfiguration.getInstance().readObjects(ProcessorFactory.PROCESSOR_FACTORIES,
                ProcessorFactory.class);
        if (processorFactories != null) {
            for (int i = 0; i < processorFactories.length; i++) {
                processorFactories[i].onInit();
            }
        }
        LogWriter.info2(logger, "已经加载的处理器工厂:%s。", String.valueOf(processorFactories));
    }

    synchronized public Processor createProcessor(Class serviceClass) {
        GeneralProcessor gp = new GeneralProcessor();
        if (processorFactories != null) {
            List<Processor> processors = new ArrayList<Processor>(processorFactories.length);
            for (int i = 0; i < processorFactories.length; i++) {
                Processor processor = processorFactories[i].createProcessor(serviceClass);
                if (processor != null) {
                    processors.add(processor);
                }
            }
            gp.setProcessors(processors.toArray(new Processor[processors.size()]));
        }
        return gp;
    }

    public void onDestroy() {
        instance = null;
    }
}
