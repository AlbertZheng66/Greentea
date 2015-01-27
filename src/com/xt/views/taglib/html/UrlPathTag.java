package com.xt.views.taglib.html;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.app.init.AppContext;
import com.xt.core.log.LogWriter;

public class UrlPathTag extends BaseFieldTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1370351254775499471L;

	private String src;

	public UrlPathTag() {
		super();
	}

	public int doStartTag() throws JspTagException {
		if (src == null && property == null) {
			throw new JspTagException("源文件路径和属性不能同时为空！");
		}
		StringBuffer content = new StringBuffer();
		AppContext ac = AppContext.getInstance();

		content.append(ac.getContextPath()).append("/");

		try {
			// 用户指定的源文件路径优先
			if (src != null) {
				content.append(src);
			} else {
				Object propertyValue = TagUtils.getInstance().lookup(
						pageContext, name, property, null);
				if (propertyValue != null) {
					content.append(propertyValue);
				}
			}

			pageContext.getOut().write(content.toString());
		} catch (IOException e) {
			throw new JspTagException("IOException msg:" + e.getMessage());
		} catch (JspException e1) {
			LogWriter.warn("未读取到名称[" + name + "]和属性[" + property + "]的值");
		}
		return SKIP_BODY;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
