package com.xt.gt.sys.loader;

import java.io.InputStream;
import java.net.URL;

import com.xt.proxy.ProxyFactory;
import com.xt.gt.sys.RunMode;

public class DefaultSystemLoader implements SystemLoader {

	public DefaultSystemLoader() {
	}

	public String getProxyType() {
		return ProxyFactory.DEFAULT_PROXY_TYPE;
	}

	public RunMode getRunMode() {
		return RunMode.APP_CLIENT;
	}

	public URL getServerURL() {
		return null;
	}

	public void setArguments(String[] args) {

	}

	public InputStream getConfigFile() {
		// 尝试从跟
		InputStream is = DefaultSystemLoader.class.getResourceAsStream("/gt-config.xml");
		return is;
	}

}
