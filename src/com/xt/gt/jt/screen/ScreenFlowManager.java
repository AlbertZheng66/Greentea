package com.xt.gt.jt.screen;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BaseException;
import com.xt.core.exception.SystemException;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 屏幕流转向控制器。
 * <p>
 * Title: XT框架-显示逻辑部分-屏幕流转向控制器
 * </p>
 * <p>
 * Description: 屏幕流处理采用职责链形式，此类定义了职责量的传递关系。
 * 每个环节都需要实现IScreenFlow接口。实现类可以通过配置文件配置，
 * 如果未配置，系统将采用默认的XmlScreenFlow和DefaultScreenFlow进行处理。
 * 首先采用框架提供的XML配置文件屏幕流，如果XML配置文件屏幕流不能处理的 屏幕（即返回的路径为空，则使用DefaultScreenFlow作为继任者）。
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
 * @date 2006-8-29
 */

public class ScreenFlowManager {

	/**
	 * 存放在request属性中的名称，如果值为”true“，则表示关闭屏幕流。
	 */
	private static final String DISABLE_SCREEN_FLOW = "DISABLE_SCREEN_FLOW";

	private static SystemConfiguration systemConfigerature = SystemConfiguration
			.getInstance();

	/**
	 * 将关闭工作流参数“DISABLE_SCREEN_FLOW”设置为true
	 */
	private static final String DISABLE_SCREEN_FLOW_Y = "Y";

	private static final String basePath = systemConfigerature.readString(
			SystemConstants.JSP_BASE_PATH, "/");

	/**
	 * 屏幕流处理链
	 */
	private static IScreenFlow[] screenFlows;

	static {
		synchronized (ScreenFlowManager.class) {
			screenFlows = (IScreenFlow[]) systemConfigerature.readObjects(
					SystemConstants.SCREEN_FLOW, IScreenFlow.class);
			if (null == screenFlows || screenFlows.length == 0) {
				screenFlows = new IScreenFlow[2];
                String screenFlowFile = systemConfigerature
					.readString(SystemConstants.SCREEN_FLOW_FILE);
			if (screenFlowFile != null) {
//				String fullName = getFullFileName(screenFlowFile);
//                // @FIXME: 屏幕流的初始化
//				// XmlScreenFlow.load(fullName);
//                DynamicDeploy.getInstance().register(XmlScreenFlow.XML_SCREEN_FLOW_FILE, fullName);
			}
				screenFlows[0] = new XmlScreenFlow(); // 系统默认采用XML屏幕流
				screenFlows[1] = new RuleScreenFlow();
			}
		}
	}

	/**
	 * 可自定义的基础路径
	 */
	private static final IScreenFlowBasePath screenFlowBasePath = (IScreenFlowBasePath) systemConfigerature
			.readObject(SystemConstants.SCREEN_FLOW_BASE_PATH, null);;

	/**
	 * 构建函数
	 */
	public ScreenFlowManager() {
	}

	/**
	 * 停止此请求的屏幕流处理
	 * 
	 * @param req
	 */
	public static void disable(HttpServletRequest req) {
		req.setAttribute(DISABLE_SCREEN_FLOW, DISABLE_SCREEN_FLOW_Y);
	}

	/**
	 * 
	 * @param req
	 * @return
	 */
	public static boolean isDisabled(HttpServletRequest req) {
		// 先检查一下屏幕流是否被关闭了
		return (DISABLE_SCREEN_FLOW_Y == req.getAttribute(DISABLE_SCREEN_FLOW));
	}

	/**
	 * 首先使用自定义的流转向的指定的JSP页,如果默认转向为空,则使用缺省的转向方式.
	 * 如果系统有自定义的JSP基础路径，则使用之（这时，系统配置的JSP_BASE_PATH参数将失效）；
	 * 否则，将使用JSP_BASE_PATH作为基础路径(其默认值是“/”)。
	 */
	public String forword(HttpServletRequest req, ActionParameter pp, Object ret)
			throws BaseException {

		String path = null;

		for (int i = 0; i < screenFlows.length; i++) {
			IScreenFlow screenFlow = screenFlows[i];
			path = screenFlow.forword(req, pp, ret);
			if (path != null) {
				break;
			}
		}

		if (path == null) {
			throw new SystemException("未根据定义的屏幕流管理器找到请求对应的页面！");
		}

		// 如果路径以“/”开头，表示决定路径
		if (path.startsWith("/")) {
			return path;
		}

		if (screenFlowBasePath != null) {
			path = screenFlowBasePath.getBasePath() + "/" + path;
		} else {
			path = basePath + "/" + path;
		}
//		LogWriter.debug("forword path", path);
		return path;
	}

}
