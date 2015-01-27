package com.xt.views.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.xt.core.utils.RandomUtils;

/**
 * 用于折叠和布局的面板（目前只完成了折叠功能）。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 将标签范围内的控件作为一个整体显示或者隐藏。
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
 * @date 2006-10-29
 */
public class PanelTag extends TagSupport {

	private static final long serialVersionUID = -6657748022168926469L;

	/**
	 * 区域的标题
	 */
	protected String title;

	/**
	 * 区域的标题
	 */
	protected String titleStyleClass = "panel";

	/**
	 * 折叠按钮的图标
	 */
	protected String buttonImage;

	protected String align = "center";

	/**
	 * 初始化时是查询选项是打开的还是关闭的
	 */
	private boolean closed = false;

	/**
	 * 区域折叠时的类
	 */
	private final static String PANEL_FOLD_UP_CLASS = "panelFoldUp";

	/**
	 * 区域折叠时的类
	 */
	private final static String PANEL_COLLAPSE_CLASS = "panelCollapse";

	// /**
	// * 是否可折叠
	// */
	// protected boolean collapsalbe;

	/**
	 * 折叠按钮的JavaScript事件,默认为“panelFoldUp”。
	 */
	protected String foldUpBtnClicked = "panelFoldUp";

	// /**
	// * 布局的方式，默认为表格布局
	// */
	// protected String layout = "table";

	public PanelTag() {
		super();
	}

	public int doStartTag() throws JspTagException {
		StringBuffer content = new StringBuffer();
		String id = createHeader(content);
		createBodyBegin(content, id);
		try {
			pageContext.getOut().write(content.toString());
		} catch (IOException e) {
			throw new JspTagException("IOException msg:" + e.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		try {
			StringBuffer content = new StringBuffer();
			createBodyEnd(content);
			pageContext.getOut().write(content.toString());
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	private String createHeader(StringBuffer strBuf) {
		// 随机产生一个折叠区域的ID
		String id = "fd" + RandomUtils.randomFixLengthNumeric(8);
		strBuf.append("	<fieldset ");
		if (titleStyleClass != null) {
			strBuf.append(" class='").append(titleStyleClass).append("'");
		}

		if (align != null) {
			strBuf.append(" align='").append(align).append("'");
		}
		strBuf.append(" >");

		strBuf.append("	<legend ");

		strBuf.append(" onclick=\"").append(foldUpBtnClicked).append("('")
				.append(id).append("')\" > ");

		// 填写legend后面的图标,span的Id规则是
		strBuf.append("<span id='").append(id).append("_span' class='");
		if (closed) {
			strBuf.append(PANEL_COLLAPSE_CLASS);
		} else {

			strBuf.append(PANEL_FOLD_UP_CLASS);
		}
		strBuf.append("' >").append(title == null ? "" : title).append(
				"</span>");

		strBuf.append("</legend>");

		return id;
	}

	private void createBodyBegin(StringBuffer strBuf, String id) {
		strBuf.append("<div ");
		strBuf.append("id='").append(id).append("'");
		strBuf.append(" >");
	}

	private void createBodyEnd(StringBuffer strBuf) {
		strBuf.append("</div></fieldset>");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getButtonImage() {
		return buttonImage;
	}

	public void setButtonImage(String buttonImage) {
		this.buttonImage = buttonImage;
	}

	public String getFoldUpBtnClicked() {
		return foldUpBtnClicked;
	}

	public void setFoldUpBtnClicked(String foldUpBtnClicked) {
		this.foldUpBtnClicked = foldUpBtnClicked;
	}

	public String getTitleStyleClass() {
		return titleStyleClass;
	}

	public void setTitleStyleClass(String titleStyleClass) {
		this.titleStyleClass = titleStyleClass;
	}

}
