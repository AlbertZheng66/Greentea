package com.xt.comm;

import com.xt.core.app.init.SystemLifecycle;
import com.xt.core.log.LogWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class Cleaner implements SystemLifecycle {

    private final Logger logger = Logger.getLogger(Cleaner.class);

    public Cleaner() {
    }

    public void onInit() {
    }

    public void onDestroy() {
//        try {
//            // 关闭 Log4j 的相关信息
//            for (Enumeration it = LogManager.getCurrentLoggers(); it.hasMoreElements();) {
//                Logger _logger = (Logger) it.nextElement();
//                _logger.removeAllAppenders();
//            }
//            LogManager.shutdown();
//        } catch (Throwable ignored) {
//            //
//        }
        LogWriter.info2(logger, "LogManager.getRootLogger()=%s", LogManager.getRootLogger());
        CleanerManager.getInstance().clean();
    }
}
