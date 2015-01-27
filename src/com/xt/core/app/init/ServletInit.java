package com.xt.core.app.init;

import java.sql.Connection;

import java.util.Map;
import javax.servlet.ServletContext;

/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description:Servlet促使化接口。Servlet初始化时，在XML中查找实现此接口的方法的名称，
 * 调用init方法，促使化实现此接口的类。XML文件的名称通过servlet_init参数得到。此方法一般用于系统
 * 启动时读取配置文件的参数时使用。一般情况下，配置文件多采用单件模式，因此如果实现类 没有默认的公共构造函数，则需要其实现静态的static方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * @deprecated 
 * @author 郑伟
 * @version 1.0
 */

public interface ServletInit {

	/**
	 * 在web.xml文件中的servlet初始化类的名称。
	 */
	public final static String SERVLET_INIT = "servlet_init";

	/**
	 * 在Servlet初始化时调用的方法
	 * 
	 * @param servletContext
	 *            Servlet上下文
	 * @throws InitializeException
	 */
	public void init(ServletContext servletContext, Map initParams,
			Connection conn) throws InitializeException;
}
