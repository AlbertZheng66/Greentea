package com.xt.gt.jt.screen;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.SystemException;
import com.xt.core.utils.ClassHelper;

/**
 * 屏幕流元素，代表一个用户自定义流处理器对象。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 这个类是一个代表了流用户自定义流处理器。
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
 * @date 2006-10-11
 */
public class FlowHadlerElement {

	/**
	 * 流处理器的类名称
	 */
	private final String name;

	/**
	 * 是否由屏幕流处理接口自己返回页面值
	 */
	private boolean direct = false;

	/*
	 * 主键是Screen的名称（流处理器处理后的返回值），值是JSP文件的路径。
	 */
	private Map<String, String> screens = new HashMap<String, String>();

	/**
	 * 根据流处理器的类名称构建一个流处理元素。如果传入的名称为空或者空串，则抛出系统异常。
	 */
	public FlowHadlerElement(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new SystemException("流处理器的名称不能为空！");
		}
		this.name = name;
	}

	/**
	 * 根据Http请求对象返回路径。首先根据流处理器的实现类名称（用户自定义的）
	 * 实例化流处理器。如果配置文件中的类未实现FlowHandler接口，则抛出系统异常。
	 * 如果流处理器返回的“屏幕”不为空，但是没有找到对应的JSP定义，则抛出系统异常。 如果流处理器返回的“屏幕”为空，则返回空。
	 * 
	 * @param request
	 *            Http请求对象，传入时不能为空。
	 * @return 流处理器的处理后的JSP路径或者空（没有对于的处理方式）。
	 */
	public String getPath(HttpServletRequest request, Object ret) {
		String path = null;
		Object flowHandler = ClassHelper.newInstance(name);
		if (flowHandler instanceof FlowHandler) {
			FlowHandler fh = (FlowHandler) flowHandler;
			String screen = fh.processFlow(request, ret);
			if (direct) {
				path = screen ; //接口的实现类直接返回屏幕
			} else {
				if (screens.containsKey(screen)) {
					path = screens.get(screen);
				} else if (screen != null) {
					throw new SystemException("未发现[" + screen + "]对应的JSP路径");
				}
			}
		} else {
			throw new SystemException("类[" + name + "]未实现接口["
					+ FlowHandler.class.getName() + "]");
		}
		return path;
	}

	public String getName() {
		return name;
	}

	public void addScreens(String screen, String path) {
		this.screens.put(screen, path);
	}

	public boolean isDirect() {
		return direct;
	}

	public void setDirect(boolean direct) {
		this.direct = direct;
	}

}
