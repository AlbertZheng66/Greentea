package com.xt.core.app;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;
import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 应用启动程序
 *
 * @author Albert
 */
public class Starter implements Runnable {

    public static final String STARTER_CLASS_NAME = "starter.class";
    /**
     * 停止的标记
     */
    private static volatile boolean stopFlag = false;
    private final static Logger logger = Logger.getLogger(Starter.class);
    /**
     * 被启动的主应用
     */
    private Startable startable;
    // 启动工作线程
    private static Thread workingThread;
    // 监听是否被终止的线程（即检查终止位是否被设置）
    private static Thread stopingThread;
    // 启动程序的线程组
    private static final ThreadGroup threadGroup = new ThreadGroup(Starter.class.getName());

    private Starter() {
    }

    private void init() {
        final Stoper stoper = reset();
        stopingThread = new Thread(threadGroup, new Runnable() {

            public void run() {
                while (!stopFlag) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        LogWriter.warn2(logger, "线程休眠被终止[%s]", ex);
                    }
                    if (stoper.isStoped()) {
                        stop();
                        break;
                    }
                }
            }
        });
        stopingThread.setName("Starter.stopingThread");
        stopingThread.start();

        // 初始化启动类
        String starterClass = System.getProperty(STARTER_CLASS_NAME);
        if (StringUtils.isEmpty(starterClass)) {
            LogWriter.warn2(logger, "系统未定义启动类参数[%s]", STARTER_CLASS_NAME);
            System.exit(1);
        }
        startable = (Startable) ClassHelper.newInstance(starterClass);

        // 开始启动
        workingThread = new Thread(threadGroup, this, "Starter.workingThread");
        workingThread.setDaemon(true);
        workingThread.start();

        // 应用关闭时自动注销
        Thread shutdownThread = new Thread(threadGroup, new Runnable() {

            public void run() {
                try {
                    // 主动停止的情况不需要再触发此调用
                    if (!stopFlag) {
                       LogWriter.info2(logger, "接受到停止信号");
                       Stoper.getInstance().stop();
                       stop();    
                    }
                } catch (Throwable t) {
                    LogWriter.warn2(logger, t, "启动器[%s]被注销时出现异常。",
                            startable);
                }
            }
        }, "Starter.shutdownThread");
        shutdownThread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        // 使用线程循环方式等待线程结束
        Stoper.getInstance().waitFor();
    }

    private Stoper reset() {
        // 启动终止应用守护线程，用于关闭应用
        final Stoper stoper = Stoper.getInstance();
        stoper.init();  // 这个状态置位（设置为：true）
        return stoper;
    }

    private void stop() {
        stopFlag = true;
        if (startable != null) {
            startable.stop();
        }
        threadGroup.interrupt();
    }

    public void run() {
        if (startable.init()) {
            LogWriter.info2(logger, "初始化启动类[%s]完成。", startable);
            startable.start();
            LogWriter.info2(logger, "启动类[%s]启动结束。", startable);
        } else {
            LogWriter.warn2(logger, "启动类[%s]初始化失败。", startable);
            stop();
        }
    }

    public static void main(String[] args) {
        // 初始化参数
        SystemLoaderManager slManager = SystemLoaderManager.getInstance();
        slManager.init(args);
        SystemConfiguration.getInstance().set(SystemConstants.APP_CONTEXT, new File("").getAbsolutePath());
        SystemLoader loader = slManager.getSystemLoader();
        if (loader.getConfigFile() != null) {
            SystemConfiguration.getInstance().load(loader.getConfigFile(), false);
        }

        final Starter starter = new Starter();
        starter.init();
    }
}
