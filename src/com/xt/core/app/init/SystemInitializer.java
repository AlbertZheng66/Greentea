package com.xt.core.app.init;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.DateUtils;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.local.LocalProxy2;
import com.xt.gt.sys.SystemConfiguration;

public class SystemInitializer {

    private final Logger logger = Logger.getLogger(SystemInitializer.class);
    /**
     * 单例实例
     */
    private static SystemInitializer instance;
    /**
     * 生命周期实例
     */
    private SystemLifecycle[] systemLifecycles;
    /**
     * 只能初始化一次。
     */
    private boolean runned = false;

    static {
        instance = (SystemInitializer) SystemConfiguration.getInstance().readObject("systemInitializer", new SystemInitializer());
    }

    private SystemInitializer() {
        systemLifecycles = (SystemLifecycle[]) SystemConfiguration.getInstance().readObjects("appLifecycles", SystemLifecycle.class);
        if (systemLifecycles != null) {
            for (int i = 0; i < systemLifecycles.length; i++) {
                SystemLifecycle systemLifecycle = systemLifecycles[i];
                LogWriter.info2(logger, "系统加载了生命周期实例[%s]。", systemLifecycle);
            }
        }
    }

    public static SystemInitializer getInstance() {
        return instance;
    }

    public synchronized void run() throws Throwable {
        if (runned) {
            return;
        }
        if (systemLifecycles != null) {
            for (int i = 0; i < systemLifecycles.length; i++) {
                run(systemLifecycles[i]);
            }
        }
        runned = true;
    }

    protected void run(SystemLifecycle lifecycle) throws Throwable {
        if (lifecycle == null) {
            return;
        }
        LogWriter.info2(logger, "SystemInit class[%s] started at [%s].........",
                lifecycle.getClass().getName(), DateUtils.toDateStr(Calendar.getInstance(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        final LocalProxy2 proxy2 = new LocalProxy2();
        try {
            // 注入参数
            Request req = new Request();
            req.setService(lifecycle.getClass());
            req.setMethodName("onInit");
            req.setParams(new Object[]{});
            req.setParamTypes(new Class[]{});
            Context context = new InitializerContext();
            proxy2.invoke(req, context);
        } catch (Throwable t) {
            //
            throw t;
        } finally {
            proxy2.onFinish();
        }
        logger.info(String.format("The SystemInit class[%s] finished at [%s]。", lifecycle.getClass().getName(), DateUtils.toDateStr(Calendar.getInstance(),
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
    }
}
