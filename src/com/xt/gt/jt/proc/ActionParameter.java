package com.xt.gt.jt.proc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.StringUtils;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * <p>
 * Title: XT框架-事务逻辑部分-请求处理参数
 * </p>
 * <p>
 * Description:请求处理参数,包含了一个HTTP请求中属于业务处理部分需要处理的各个参数. 参数中主要包含了三个参数，1.
 * 包/路径：将业务处理类进行分类打包，一般情况下，对应于业务处理类和JSP文件 的路径，以便于管理。2. bizHandler：对应于业务处理类。3.
 * dealMethod:对应于一个业务处理类中的处理方法。 应用可以自定义业务处理方法的名称，系统默认为“method”。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-5
 */
public class ActionParameter {

	/**
	 * 包的层集结构,指用户为了便于管理,将不同的公共存放在不同的包里面. 这个包结构可以分为多层,也可以设置为空.
	 */
	private List<String> packages = new ArrayList<String>(1);

	private String bizHandler;

	private String dealMethod;

	private static final String ACTION_PARAMETER_IN_REQUEST = "ACTION_PARAMETER_IN_REQUEST";

	// 请求的后缀
	private String subfix = SystemConfiguration.getInstance().readString(
			SystemConstants.REQUEST_SUBFIX, ".do");

	/**
	 * 请求的处理方法名称(URL参数中用来传递处理方法的参数名称)
	 */
	private String methodName = SystemConfiguration.getInstance().readString(
			SystemConstants.REQUEST_METHOD_PARAM, "method");

	private ActionParameter() {
	}

	public static ActionParameter getInstance(RequestEvent requestEvent) {
		HttpServletRequest request = requestEvent.getRequest();
		// 将解析后的参数保存起来
		if (request.getAttribute(ACTION_PARAMETER_IN_REQUEST) != null) {
			ActionParameter ap = (ActionParameter) request
					.getAttribute(ACTION_PARAMETER_IN_REQUEST);
			return ap;
		}
		ActionParameter ap = new ActionParameter();
		ap.parse(requestEvent);
		request.setAttribute(ACTION_PARAMETER_IN_REQUEST, ap);
		return ap;
	}

	private void parse(RequestEvent requestEvent) {
		// Http请求
		HttpServletRequest request = requestEvent.getRequest();

		String path = request.getServletPath();
		// 将请求按照"/"分段,假设提交的URL中不会包含"\",有服务器处理
		String[] segs = path.split("/");
		if (segs == null || segs.length == 0) {
			throw new SystemException("提交的HTTP[" + path + "]请求不正确!");
		}

		// 过滤掉空格
		List<String> segList = new ArrayList<String>(segs.length);
		for (int i = 0; i < segs.length; i++) {
			if (segs[i] != null && segs[i].trim().length() > 0) {
				segList.add(segs[i]);
			}
		}
		for (int i = 0; i < segList.size() - 1; i++) {
			packages.add(segList.get(i));
		}

		// 最后一段是业务处理类（bizHandler）
		String seg = (String) segList.get(segList.size() - 1);

		if (seg.endsWith(subfix)) {
			bizHandler = seg.substring(0, seg.length() - subfix.length());
		}

		// 业务处理方法（dealMethod），用户可以自定义业务处理方法的名称，系统默认为“method”
		dealMethod = requestEvent.getParameter(methodName);

	}

	/**
	 * 一般情况下，bizHadler和method是不能为空的
	 * 
	 * @param pp
	 * @return 是否相等
	 */
    @Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ActionParameter)) {
			return false;
		}
		ActionParameter pp = (ActionParameter) obj;
		if (pp.bizHandler == null || bizHandler == null
				|| !bizHandler.equals(pp.bizHandler)) {
			return false;
		}
		if (pp.dealMethod == null || dealMethod == null
				|| !dealMethod.equals(pp.bizHandler)) {
			return false;
		}

		String ppPaks = pp.getAllPackages();
		String pks = getAllPackages();
		if (ppPaks != null && !ppPaks.equals(pks)) {
			return false;
		}
		if (pks != null && !pks.equals(ppPaks)) {
			return false;
		}
		return true;
	}

    @Override
	public int hashCode() {
		int code = 0;
		if (bizHandler != null) {
			code = code + bizHandler.hashCode();
		}
		if (dealMethod != null) {
			code = code + dealMethod.hashCode();
		}
		if (packages != null) {
			code = code + packages.hashCode();
		}
		LogWriter.debug("code", code);
		return code;
	}

    @Override
	public String toString() {
		StringBuilder strBuf = new StringBuilder();
		if (bizHandler != null) {
			strBuf.append(bizHandler);
		}
		strBuf.append("@");
		if (dealMethod != null) {
			strBuf.append(dealMethod);
		}
		strBuf.append("@");
		if (packages != null) {
			strBuf.append(packages.toString());
		}
		return strBuf.toString();
	}

	public String getBizHandler() {
		return bizHandler;
	}

	public void setBizHandler(String bizHandler) {
		this.bizHandler = bizHandler;
	}

	public String getMethod() {
		return dealMethod;
	}

	public void setMethod(String method) {
		this.dealMethod = method;
	}

	public List getPackages() {
		return packages;
	}

	/**
	 * 将包的层集结构以字符串方式返回,
	 * 
	 * @return
	 */
	public void setAllPackages(String packageStr) {
		// 校验packageStr是否为合法的字符串，即只有字母，数字和下划线和反斜杠“/”组成的字符串
		if (null != packageStr) {
			if (packageStr.indexOf('/') > -1) {
				String[] segs = packageStr.split("/");
				for (int i = 0; i < segs.length; i++) {
					if (segs[i].trim().length() > 0) {
						packages.add(segs[i]);
					}
				}
			} else {
				packages.add(packageStr);
			}

		}
	}

	/**
	 * 将包的层集结构以字符串方式返回,
	 * 
	 * @return
	 */
	public String getAllPackages() {
		if (null != packages) {
			return StringUtils.join(packages, "/");
		}
		return "";
	}
}