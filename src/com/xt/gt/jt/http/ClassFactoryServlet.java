package com.xt.gt.jt.http;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.log.LogWriter;

public class ClassFactoryServlet extends HttpServlet {
	
	private final Logger logger = Logger.getLogger(ClassFactoryServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7597303458012678691L;

	public void init() throws ServletException {
		LogWriter.info(logger, "ClassFactoryServlet: Initialization complete.....");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, java.io.IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, java.io.IOException {

		// 接收请求时间（开始时间）
		LogWriter.debug(logger, "ClassFactoryServlet startTime=", System.currentTimeMillis());

//		String oldClassName = req.getParameter("oldClassName");
//		String newClassName = req.getParameter("newClassName");
		String className = req.getParameter("className");
		if (className != null) {
			try {
                byte[] classBytes = ClassModifierFactory.getInstance().getClassByteCode(className);
				OutputStream os = res.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(os);
				bos.write(classBytes);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
