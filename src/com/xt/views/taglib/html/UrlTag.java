package com.xt.views.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.xt.core.app.init.AppContext;

public class UrlTag extends TagSupport {

	/**
	 * 数据源
	 */
	protected String href;

	protected String styleClass;

	protected String title;

	/**
	 */
	protected String onclick;

	public UrlTag() {
		super();
	}

	public int doStartTag() throws JspTagException {
		StringBuffer content = new StringBuffer();

		AppContext ac = AppContext.getInstance();

		content.append("<a href='").append(ac.createContextPath(href)).append(
				"'>");
		if (title != null) {
			content.append(title);
		}
		content.append("</a>");
		try {
			pageContext.getOut().write(content.toString());
		} catch (IOException e) {
			throw new JspTagException("IOException msg:" + e.getMessage());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {

		return SKIP_BODY;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
