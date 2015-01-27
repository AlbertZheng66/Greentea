package com.xt.gt.jt.screen;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.log.LogWriter;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 对应于一个Action的屏幕流参数
 * <p>
 * Title: XT框架-视图逻辑部分
 * </p>
 * <p>
 * Description: 此类定义了一个使用XML文件定义的屏幕流的结构，即一个服务处理URL
 * （由package和bizHadler唯一决定）的屏幕流对应关系 一般与业务类一致（包含多个业务方法）。
 * 
 * 首先查找用户定义的dealMethod，如果用户在XML中定义了方法，则由此定 义处理屏幕流。如果未定义根据dealMethod参数处理的屏幕流，
 * 则继续考察是否有合适的屏幕流处理器， 如果自定义了屏幕处理器进行处理。
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
 * @date 2006-8-30
 */
public class ActionElement {

	/**
	 * HTTP请求的路径，服务所在的包
	 */
	private String packages;

	/**
	 * 请求方法
	 */
	private String bizHadler;
	
	private final static String STAR = "*"; 

	/**
	 * 通过处理方法来控制页面的流转
	 */
	private Map<String, DealMethodElement> dealMethodFlows = new HashMap<String, DealMethodElement>();

	/**
	 * 通过流处理器控制页面的流转
	 */
	private FlowHadlerElement flowHadler;

	public ActionElement() {

	}

	/**
	 * 根据HTTP请求和处理参数返回指定的处理页面。首先根据业务方法（dealMethod）参数
	 * 进行匹配，如果相应的屏幕流配置，则其优先；否则，由流处理类的实现类进行处理。 如果这两种情况均不能匹配，则返回空。
	 * 
	 * @param req
	 *            HTTP请求
	 * @param pp
	 *            处理参数
	 * @return
	 */
	public String getPath(HttpServletRequest req, ActionParameter pp, Object ret) {
		String path = null;
		DealMethodElement dmf = dealMethodFlows.get(pp.getMethod());
		
		// 如果流处理器为空，则屏幕流参数是必须的，否则，可以没有屏幕流参数
		if (dmf != null) {
			path = dmf.getPath(req, ret);
		} else if (dealMethodFlows.containsKey(STAR)) {
			dmf = dealMethodFlows.get(STAR);
			path = dmf.getPath(req, ret);
		} 
//		LogWriter.debug("getPath path", path);
//		LogWriter.debug("getPath flowHadler", flowHadler);
		if (path == null && flowHadler != null) {
			path = flowHadler.getPath(req, ret);
		}
//		LogWriter.debug("getPath path", path);
		return path;
	}

	/**
	 * 增加一个业务处理方法元素。
	 * 
	 * @param dmf
	 */
	public void put(DealMethodElement dmf) {
		if (dmf != null && dmf.getDealMethod() != null) {
			dealMethodFlows.put(dmf.getDealMethod(), dmf);
		}
	}

	public String getBizHadler() {
		return bizHadler;
	}

	public void setBizHadler(String bizHadler) {
		this.bizHadler = bizHadler;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public FlowHadlerElement getFlowHadler() {
		return flowHadler;
	}

	public void setFlowHadler(FlowHadlerElement flowHadler) {
		this.flowHadler = flowHadler;
	}
}

/**
 * <action package="account" bizHadler ="DatAcctOutputFile" > <dealMethod
 * name="add" variable="screen" path="/${default}/AddDatAcctOutputFileLog.jsp">
 * <variable value="gotoAdd" path="/${default}/AddDatAcctOutputFileLog.jsp" />
 * <variable value="list" path="/${default}/AddDatAcctOutputFileLog.jsp" />
 * </dealMethod> <flowHandler name= "com.xt.demo.OutputFlowHandler"> <screen
 * name="ListDatAcctOutputFileLog"
 * path="/${default}/ListDatAcctOutputFileLog.jsp" /> <screen
 * name="AddDatAcctOutputFileLog"
 * path="/${default}/ListDatAcctOutputFileLog.jsp" /> </flowHandler> </action>
 */
