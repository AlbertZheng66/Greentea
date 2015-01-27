
package com.xt.gt.sys.loader;

import org.apache.commons.lang.StringUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;

/**
 * 系统加载器管理器，用于确定应用的加载模式。
 * @author albert
 */
public class SystemLoaderManager {
    
    private static SystemLoaderManager instance = new SystemLoaderManager();
    
    private SystemLoader systemLoader = new DefaultSystemLoader();
    
    private SystemLoaderManager() {        
    }
    
    
    static public SystemLoaderManager getInstance() {
        return instance;
    }
    
    /**
     * 初始化，应该在主程序启动时马上调用此方法。不建议在业务逻辑处理程序中调用此方法。
     * 首先，从系统参数中读取系统装载器的名称（此名从应该是装载器实现类的名称），如果未读取到；
     * 再次，从命令行参数中读取。
     * @param args
     */
    public void init(String[] args) {      
    	CommandLineSystemLoader clsl = new CommandLineSystemLoader();
    	clsl.setArguments(args);
    	String sysLoaderString = clsl.getSystemLoader();
        if (StringUtils.isEmpty(sysLoaderString)) {
            sysLoaderString = System.getProperty(SystemLoader.SYSTEM_LOADER_NAME);
        }
        LogWriter.debug("sysLoaderString", sysLoaderString);
        if (StringUtils.isNotEmpty(sysLoaderString)) {
            systemLoader = (SystemLoader)ClassHelper.newInstance(sysLoaderString);
        } else {
        	systemLoader = new DefaultSystemLoader();
        }
        systemLoader.setArguments(args);
    }
    
    /**
     * 直接使用系统装载器变量进行初始化（一般情况下，服务器端采用这种方式进行初始化）。
     * @param systemLoader
     */
    public void init(SystemLoader systemLoader) {        
        this.systemLoader = systemLoader;
    }    
    
    public SystemLoader getSystemLoader() {
        return systemLoader;
    }

}
