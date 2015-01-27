package com.xt.core.app.init;

import java.sql.Connection;

import javax.servlet.ServletContext;

import com.xt.core.log.LogWriter;
import java.util.Map;

/**
 * @deprecated 
 * @author albert
 */
public class AppContext implements ServletInit {
	

	public String rootPath = "";
	
	public String contextPath;
	
	private static AppContext instance = new AppContext();

	private AppContext() {

	}

	public static AppContext getInstance() {
		return instance;
	}

	/**
	 * 返回应用发布的绝对路径
	 * @return
	 */
	public String getRootPath() {
		return rootPath;
	}
	
	/**
	 * 返回应用发布的上下文路径(应用名称)
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	public void init(ServletContext sc, Map hm, Connection conn)
			/*throws InitializeException*/{
		rootPath = sc.getRealPath("/");
		LogWriter.debug("rootPath", rootPath);
		
		 /**
		  * 目前无法通过程序动态的得到应用的上下文，Web.xml的"display name"进行配置（可移植性有待进一步处理）；
		  * 注意：这个上下文应该和程序的发布路径保持一致，否则，在某些图片程序处理时将产生错误！
		  */
		contextPath = sc.getServletContextName();
		if (contextPath == null) {
			contextPath = "/";
		}
		if (!contextPath.startsWith("/")) {
			contextPath = "/" + contextPath;
		}
		
		
		LogWriter.debug("contextPath", contextPath);
	}
	
	public String createContextPath(String path) {
		StringBuilder strBld = new StringBuilder(contextPath);
		if (!path.startsWith("/")) {
			strBld.append("/");
		}
		strBld.append(path);
		return  strBld.toString(); 
	}
}
