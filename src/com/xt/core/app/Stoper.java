
package com.xt.core.app;

import com.xt.core.log.LogWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class Stoper {
    private final static Logger logger = Logger.getLogger(Stoper.class);
    
    private final Terminator terminator = new PropertyTerminator();
    
    private static final Stoper instance = new Stoper();
    
     public void waitFor() {
        // 使用线程循环方式等待线程结束
        while (!isStoped()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                // do nothing.
                LogWriter.warn2(logger, ex, "The thread is interrpted.");
            }
        }
    }
    
    private Stoper() {
    }
    
    static public Stoper getInstance() {
        return instance;
    }
    
    public void init() {
        terminator.init(null);
    }
    
    public boolean isStoped() {
        return terminator.isStoped(null);
    }
    
    public void stop() {
        terminator.stop(null);
    }
    
    public static void main(String[] args) {
        LogWriter.info2(logger, "尝试暂停服务器。");
        
        // 初始化参数
//        SystemLoaderManager slManager = SystemLoaderManager.getInstance();
//        slManager.init(args);
//        SystemConfiguration.getInstance().set(SystemConstants.APP_CONTEXT, new File("").getAbsolutePath());
//        SystemLoader loader = slManager.getSystemLoader();
//        if (loader.getConfigFile() != null) {
//            SystemConfiguration.getInstance().load(loader.getConfigFile(), false);
//        }
        
        Stoper.getInstance().stop();
    }
    
}
