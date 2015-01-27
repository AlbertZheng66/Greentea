package com.xt.gt.jt.screen;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.SystemException;
import com.xt.gt.jt.event.HttpRequestEvent;
import com.xt.gt.jt.event.RequestEvent;

/**
 * 由处理方法参数控制流转的屏幕流。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 处理方法参数控制流转的屏幕流元素，对应于配置文件的“dealMethod”标签。如下： <action
 * package="account" bizHadler ="DatAcctOutputFile" > <dealMethod name="add"
 * variable="screen" default="/account/AddDatAcctOutputFileLog.jsp"> <variable
 * value="gotoAdd" path="/account/AddDatAcctOutputFileLog.jsp" /> <variable
 * value="list" path="/account/AddDatAcctOutputFileLog.jsp" /> </dealMethod>
 * </action>
 * </p>
 * <p>
 * 当一个业务处理方法定义了这种屏幕流控制方式时，需要定义一个“variable”变量(如：variable="screen")，
 * 系统将根据这个变量的请求参数值（通过“request.getParameter("screen")”得到）决定调整
 * 的页面。dealMethod标签的default为缺省值。
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
 * @date 2006-8-13
 */
public class DealMethodElement {

	/**
	 * 默认的变量名称。如果用户为定义变量名称，则采用此名称
	 */
	public static final String DEFAULT_VARIABLE_NAME = "_screen";

	/**
	 * 处理方法的名称，不能为空。
	 */
	private String dealMethod;

	/**
	 * 变量的名称，用来控制页面调整的变量。variableName可以为空，表示此方法对应的路径为默认路径。
	 */
	private String variableName;

	/**
	 * 处理方法对应的默认路径。如果variableName变量为空，则此路径不能为空。
	 */
	private String defaultPath;

	/**
	 * 对应于变量名称的变量值，键是变量的值，值是JSP的路径。
	 */
	private Map<String, String> variables = new HashMap<String, String>();
	
	/**
	 * 通过流处理器控制页面的流转
	 */
	private FlowHadlerElement flowHadler;

	public DealMethodElement() {

	}

	/**
	 * 返回JSP页面的路径。如果没有定义变量，则采用默认路径。如果没有发现变量名称对应的值，
	 * 则抛出异常。如果用户显式定义了路径，则采用之；否则，则采用默认路径。
	 * 
	 * @param request HTTP请求参数
	 * @param needScreen 屏幕流参数是否是必须的
	 * @return
	 */
	public String getPath(HttpServletRequest request, Object ret) {
		
		// 默认路径
		String path = defaultPath;
		
		if (null == variableName) {
			variableName = DEFAULT_VARIABLE_NAME;
		}

		// 未发现变量对应的参数
		RequestEvent requestEvent = HttpRequestEvent.getInstance(request);
		String value = requestEvent.getParameter(variableName);
//		LogWriter.debug("DealMethodElement getPath variableName", variableName);
//		LogWriter.debug("DealMethodElement getPath value", value);
		if (value == null) {
            if (flowHadler == null) {
            	throw new SystemException("未发现变量[" + variableName + "]对应的屏幕参数！");
			}
		} else if (variables.containsKey(value)) {
			// 如果用户没有显示定义，则采用默认路径
			path = variables.get(value);
		}
		
		if (path == null && flowHadler != null) {
        	path = flowHadler.getPath(request, ret);
		} 

		return path;
	}

	public String getDealMethod() {
		return dealMethod;
	}

	public void setDealMethod(String dealMethod) {
		this.dealMethod = dealMethod;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void setVariables(String varialbleValue, String path) {
		variables.put(varialbleValue, path);
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}

	public FlowHadlerElement getFlowHadler() {
		return flowHadler;
	}

	public void setFlowHadler(FlowHadlerElement flowHadler) {
		this.flowHadler = flowHadler;
	}
	
	
}
