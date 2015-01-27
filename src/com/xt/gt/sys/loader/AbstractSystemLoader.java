package com.xt.gt.sys.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.RunMode;

abstract public class AbstractSystemLoader implements SystemLoader {
	
	private static Logger logger = Logger.getLogger(AbstractSystemLoader.class); 

	protected RunMode runMode;

	protected String serverURLString;

	protected String proxyType;

	protected String configFileName = "gt-config.xml";

	public AbstractSystemLoader() {
	}

	public void setArguments(String[] args) {
		// 首先尝试从属性中读取信息
		PropertySystemLoader psl = new PropertySystemLoader();
		runMode = psl.getRunMode();
		serverURLString = psl.serverURLString;
		proxyType = psl.getProxyType();
		if (psl.getConfigFileName() != null) {
			configFileName = psl.getConfigFileName();
		}

		// 如果有命令行设置，则参数设置优先(如果设置，将覆盖属性中的相同信息)
		CommandLineSystemLoader clsl = new CommandLineSystemLoader();
		clsl.setArguments(args);
		if (clsl.getRunMode() != null) {
			runMode = clsl.getRunMode();
		}
		if (clsl.serverURLString != null) {
			serverURLString = clsl.serverURLString;
		}
		if (clsl.getProxyType() != null) {
			proxyType = clsl.getProxyType();
		}
		if (clsl.getConfigFileName() != null) {
			configFileName = clsl.getConfigFileName();
		}
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public InputStream getConfigFile() {
		if (StringUtils.isEmpty(configFileName)) {
			return null;
		}
		InputStream is = AbstractSystemLoader.class
				.getResourceAsStream("/" + configFileName);
		if (is != null) {
			LogWriter.info(logger, String.format("从文件流[/%s]中加载配置文件成功。", configFileName));
			return is;
		}
		try {
			LogWriter.info(logger, String.format("从文件系统[%s]中加载配置文件成功。", configFileName));
			is = new FileInputStream(configFileName);
		} catch (FileNotFoundException e) {
			// nothing to do;
			LogWriter.warn(logger, String.format("配置文件[%s]加载失败。", configFileName), e);
		}
		return is;
	}

	public String getProxyType() {
		return proxyType;
	}

	public RunMode getRunMode() {
		return runMode;
	}

	public URL getServerURL() {
		if (StringUtils.isNotEmpty(serverURLString)) {
			try {
				return new URL(serverURLString);
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
				throw new SystemException("URL 格式错误。", ex);
			}
		}
		return null;
	}

}
