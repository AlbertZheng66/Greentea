package com.xt.proxy.impl.http.hessian;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

/**
 * 对Hessian原有的Servlet进行包装，以便进行系统初始化等操作。
 * @author albert
 *
 */
public class HessianServletWrapper extends HessianServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2995552194424274579L;
	
	public HessianServletWrapper() {
		
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 初始化加载！
	}
	
	
	

}
