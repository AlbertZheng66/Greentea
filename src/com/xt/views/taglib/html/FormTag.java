package com.xt.views.taglib.html;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.html.Constants;

import com.xt.core.log.LogWriter;

/**
 * Custom tag that represents an input form, associated with a bean whose
 * properties correspond to the various fields of the form.
 * 
 * @version $Rev: 331056 $ $Date: 2006/09/17 00:46:37 $
 */
public class FormTag extends org.apache.struts.taglib.html.FormTag {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8007993142119067177L;
	
	public static final String BEAN_NAME = Constants.BEAN_KEY;
	
	public FormTag () {
		super();
	}

	// ------------------------------------------------------ Protected Methods
	
	/**
	 * 覆盖掉父类的方法，替换成本框架的值处理方式。
	 * 
	 * @exception JspException
	 *                if a required value cannot be looked up
	 */
	protected void initFormBean() throws JspException {
		int scope = PageContext.SESSION_SCOPE;
        if ("request".equalsIgnoreCase(beanScope)) {
            scope = PageContext.REQUEST_SCOPE;
        }
		Object bean = pageContext.getAttribute(beanName, scope);
		
		
		
		/**
		 * @todo:有没有必要全局搜索Bean？
		 */
//		if (bean == null) {
//		     bean = pageContext.findAttribute(beanName);
//		}
		if (bean == null) {
		     LogWriter.warn("FormTag的标签没有赋值！");
		     bean = new Object();
		}
	    pageContext.getRequest().setAttribute(FormTag.BEAN_NAME, bean);
		super.initFormBean();
	}


	/**
	 * 覆盖掉父类的方法，替换成本框架的值处理方式。
	 * 
	 * @exception JspException
	 *                if a required value cannot be looked up
	 */
	protected void lookup() throws JspException {
		 // Calculate the required values
		 beanName = BEAN_NAME;
		 beanScope = "request";   //struts的标签将其转换为PageContext.REQUEST_SCOPE;
		 beanType = "formBeanConfig.getType()";
		 LogWriter.debug("BEAN_NAME(FormTag)", 
				 pageContext.getRequest().getAttribute(FormTag.BEAN_NAME));
	}
}
