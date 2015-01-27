package com.xt.views.taglib.html;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.xt.core.app.init.AppContext;
import com.xt.core.message.BaseMessage;
import com.xt.core.message.Messages;
import com.xt.gt.jt.event.HttpRequestEvent;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 用于显示错误和信息的标签。
 * <p>
 * Title: XT框架-GreenTea标签库
 * </p>
 * <p>
 * Description: 此标签用于显示错误和提示信息。如果出现了错误信息，提示信息将不起作用。
 * 标签首先在“request”的“ERROR_MESSAGE_IN_REQUEST”属性中读取错误信息；如果没有，
 * 再到“MESSAGES_IN_REQUEST”属性中寻找提示信息。 找到错误消息后将这些小时显示在一个表格中。
 * 定义消息的CSS（即styleId）分别为：error和message
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
 * @date 2006-10-17
 */
public class MessagesTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7871348961086527524L;

	public static final String ERROR_MESSAGE_IN_REQUEST = "SYS_ERROR_MSG";

	public static final String MESSAGES_IN_REQUEST = "MESSAGES_IN_REQUEST";

	private static final String SYSTEM_ERROR_MSG_CLASS;

	private static final String ERROR_MSG_CLASS;

	private static final String INFO_MSG_CLASS;

	private static final String OK_MSG_CLASS;

	private static final String CLOSE_IMG = "/images/close.gif";

	/**
	 * 操作成功时显示的提示信息
	 */
	private String info;

	/**
	 * 在指定的操作时才显示提示信息
	 */
	private String infoMethod;

	static {
		TagProperty tp = TagProperty.newInstance();
		SYSTEM_ERROR_MSG_CLASS = tp.getProperty("system_error_msg_class");
		ERROR_MSG_CLASS = tp.getProperty("error_msg_class");
		INFO_MSG_CLASS = tp.getProperty("info_msg_class");
		OK_MSG_CLASS = tp.getProperty("ok_msg_class");
	}

	/**
	 * 错误消息优先
	 */
	public int doStartTag() throws JspException {
		// LogWriter.debug("doStartTag... infoMethod=", infoMethod);
		ServletRequest request = pageContext.getRequest();
		String errMsg = (String) request.getAttribute(ERROR_MESSAGE_IN_REQUEST);
		String css = null; //
		String msg = null;
		if (StringUtils.isNotEmpty(errMsg)) {
			msg = errMsg;
			css = ERROR_MSG_CLASS;
		} else if (request.getAttribute(MESSAGES_IN_REQUEST) != null) {
			Messages messages = (Messages) request
					.getAttribute(MESSAGES_IN_REQUEST);
			css = INFO_MSG_CLASS;
			String[] infos = new String[messages.getMessages().size()];
			for (int i = 0; i < infos.length; i++) {
				BaseMessage bm = (BaseMessage) messages.getMessages().get(i);
				infos[i] = bm.getCode();
			}
			msg = StringUtils.join(infos, ";");
		} else if (infoMethod != null) {
			RequestEvent requestEvent = HttpRequestEvent
					.getInstance((HttpServletRequest) request);

			ActionParameter actionParameter = ActionParameter
					.getInstance(requestEvent);
			if (infoMethod.contains(actionParameter.getMethod())) {
				msg = parseMessage(actionParameter.getMethod());
				msg = (msg == null ? "" : msg);
				css = OK_MSG_CLASS;
			}
		}
		boolean hasMsg = true; // 是否显示关闭图标
		if (StringUtils.isEmpty(msg)) {
			msg = "&nbsp;";
			hasMsg = false;
		}
		try {
			pageContext.getOut().write(createMsgBody(css, msg, hasMsg));
		} catch (IOException e) {
			throw new JspException("标签的IO操作出现异常", e);
		}

		return (SKIP_BODY);

	}

	private String parseMessage(String methodName) {
		String[] methodSegs = infoMethod.split(";");
		String[] infoSegs = info.split(";");
		for (int i = 0; i < methodSegs.length; i++) {
			if (methodName != null && methodName.equals(methodSegs[i])) {
				if (i < infoSegs.length) {
					return infoSegs[i];
				}
			}
		}
		return null;
	}

	/**
	 * 根据样式单和消息创建显示消息的表
	 * 
	 * @param css
	 * @param msg
	 * @return
	 */
	private String createMsgBody(String css, String msg, boolean hasMsg) {
		StringBuffer body = new StringBuffer();
		body.append("<div id='_message_bar'");
		if (hasMsg) {
			body.append(" style='display:block;width:95%;'");
		} else {
			body.append(" style='display:none;width:95%;'");
		}
		body.append(">");
		body.append("<div id='_message_area' ");
		body.append(" style='text-align:left;float:left;clear:left;overflow:hidden;'");
		if (css != null) {
			body.append(" class='").append(css).append("'");
		}
		body.append(">");

		if (msg != null) {
			body.append(msg);
		}

		body.append("</div>");

		// 创建一个关闭按钮
		body.append("<div");
		body.append(" style='float:right;clear:right; overflow:hidden'");
		body.append(">");
		
		body.append("<img id='_message_close_btn'");
		body.append(" src='");
		body.append(AppContext.getInstance().createContextPath(CLOSE_IMG));
		body.append("' onclick='closeMsg()'  />");
		body.append("</div>");

		body.append("</div>"); // end of _message_bar
		return body.toString();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfoMethod() {
		return infoMethod;
	}

	public void setInfoMethod(String infoMethod) {
		this.infoMethod = infoMethod;
	}

}

