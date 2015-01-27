package com.xt.comm.quartz;

import com.xt.core.app.init.SystemLifecycle;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 位于服务器端的启动信息。
 * @author albert
 */
public class ServerStarter implements SystemLifecycle {

    private Scheduler scheduler = null;

    private final Logger logger = Logger.getLogger(ServerStarter.class);
    /**
     * 系统默认加载的配置文件名称，${appContext}/WEB-INF/quartz.properties。
     * 目前所有文件路径都采用统一的分隔符（“/”）。
     */
    private static final String defaultFileName = SystemConfiguration.getInstance().readString(SystemConstants.APP_CONTEXT, "./") + "WEB-INF/quartz.properties";
    /**
     * Quartz 使用的配置文件的名称，可通过参数“quartz.fileName”进行配置。
     */
    private final String fileName = SystemConfiguration.getInstance().readString("quartz.fileName", defaultFileName);

    public ServerStarter() {
    }

    /**
     * 在系统启动时加载服务器
     */
    public void onInit() {
        if (StringUtils.isEmpty(fileName)) {
            LogWriter.warn2(logger, "定时任务的配置文件为空。");
            return;
        }
        try {
            LogWriter.info2(logger, "正在加载定时任务的配置文件[%s]......", fileName);
            SchedulerFactory scheduleFactory = new StdSchedulerFactory(fileName);
            scheduler = scheduleFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException ex) {
            throw new SystemException("启动定时任务失败。", ex);
        }
    }

    public void onDestroy() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown(false);
                scheduler = null;
            }
        } catch (Throwable ex) {
            LogWriter.warn2(logger, ex, "关闭任务调度失败。");
        }
    }
}
