
package com.xt.gt.sys.loader;

import java.io.InputStream;
import java.net.URL;

import com.xt.gt.sys.RunMode;

/**
 * 服务器系统启动器，在服务器端进行启动加载器。
 * @author albert
 */
public class ServerSystemLoader implements SystemLoader {
	
	private static final ServerSystemLoader instance = new ServerSystemLoader();
	
	private InputStream inputStream;
	
	private ServerSystemLoader() {		
	}
	
	public static ServerSystemLoader getInstance() {
		return instance;
	}
	
	
    public void setArguments(String[] args) {
    }

    public RunMode getRunMode() {
        return RunMode.SERVER;
    }

    public URL getServerURL() {
        return null;
    }

    public String getProxyType() {
        return null;
    }

	public InputStream getConfigFile() {
		return inputStream;
	}
	
	public void setConfigFile(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
