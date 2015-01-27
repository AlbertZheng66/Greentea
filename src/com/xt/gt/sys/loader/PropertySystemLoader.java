
package com.xt.gt.sys.loader;

import org.apache.commons.lang.StringUtils;

import com.xt.gt.sys.RunMode;

/**
 * 从系统属性（使用：System.getProperty("ParamName")）中读取参数。
 * @author albert
 */
public class PropertySystemLoader extends AbstractSystemLoader {
	
	public PropertySystemLoader() {
		String runModeString = System.getProperty("RunMode");
        if (StringUtils.isNotEmpty(runModeString)) {
        	runMode = RunMode.valueOf(runModeString);
        }
        serverURLString = System.getProperty("ServerURL");
        proxyType = System.getProperty("ProxyType");
        configFileName = System.getProperty("ConfigFileName");
	}

    public void setArguments(String[] args) {
    	// 覆盖掉抽象方法中的同名函数，避免从命令行中加载        
    }

}
