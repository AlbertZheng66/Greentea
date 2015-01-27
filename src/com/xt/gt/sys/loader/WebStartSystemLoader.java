package com.xt.gt.sys.loader;

import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.SystemException;
import com.xt.gt.sys.RunMode;

/**
 * 
 * @author albert
 */
public class WebStartSystemLoader extends AbstractSystemLoader {

	public WebStartSystemLoader() {
	}

	public RunMode getRunMode() {
		return RunMode.WEB_START_CLIENT;
	}

	public URL getServerURL() {
		try {
			// Lookup the javax.jnlp.BasicService object
			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			URL codeBase = bs.getCodeBase();
			// TODO: 如果serverURLString以http开头，则任务用户定义了一个新的完整的URL
			if (StringUtils.isEmpty(serverURLString)) {
				return codeBase;
			} else {
                URL fullURL = new URL(codeBase.getProtocol(), 
                		codeBase.getHost(), codeBase.getPort(), codeBase.getPath() + serverURLString);
                return fullURL;
			}
		} catch (Exception ue) {
			throw new SystemException("读取服务器参数失败。", ue);
		}
	}

}
