package com.xt.views.taglib.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import com.xt.core.log.LogWriter;
import com.xt.gt.jt.event.HttpRequestEvent;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.views.taglib.html.fc.FieldControllerFactory;

public class ButtonTag extends
org.apache.struts.taglib.html.ButtonTag  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4230645451038303727L;

	protected String fieldController;
	
	private boolean visiable = true;
	
	public ButtonTag() {
		super();
	}
	
	public int doStartTag() throws JspException {
//		LogWriter.debug("doStartTag fieldController", fieldController);
		if (StringUtils.isNotEmpty(fieldController)) {
			try {
				
				ParamParser pp = ParamParser.getInstance(fieldController);
				String name = pp.getName();
				String params = pp.getParams();
				IFieldController controller = FieldControllerFactory.getInstance().get(name);
				HttpServletRequest hsr = (HttpServletRequest) pageContext
						.getRequest();
				RequestEvent re = HttpRequestEvent.getInstance(hsr);
				visiable = controller.isVisiable(re, params);
				
				if (!visiable) {
					// 不可见则跳过此输入域的显示()
					return SKIP_BODY;
				}
				if (controller.isReadonly(re, params)) {
					setReadonly(true);
				}
			} catch (RuntimeException e) {
				LogWriter.error("域控制器调用产生异常！", e);
				throw new JspException("域控制器调用产生异常！", e);
			}
		}

		return super.doStartTag();
	}
	
	public int doEndTag() throws JspException {
		if (!visiable) {
			// 不可见则跳过此输入域的显示()
			return SKIP_BODY;
		}
		return super.doEndTag();
	}

	public String getFieldController() {
		return fieldController;
	}

	public void setFieldController(String fieldController) {
		this.fieldController = fieldController;
	}

	
}
