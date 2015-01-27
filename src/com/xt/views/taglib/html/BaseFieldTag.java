/*
 * $Header: f:/cvsroot/core/src/com/xt/views/taglib/html/BaseFieldTag.java,v 1.3 2006/09/17 00:46:36 zhengwei Exp $
 * $Revision: 1.3 $
 * $Date: 2006/09/17 00:46:36 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.xt.views.taglib.html;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.formater.Formater;
import com.xt.core.utils.formater.FormaterUtils;
import com.xt.gt.jt.event.HttpRequestEvent;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.views.taglib.html.fc.FieldControllerFactory;

/**
 * 增强的Struts标签。
 * <p>
 * Title: XT框架-显示逻辑部分
 * </p>
 * <p>
 * Description: 在Struts标签库的基础上扩展了校验、初始化和格式化功能。
 * 
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
 * @date 2006-9-26
 */
public abstract class BaseFieldTag extends
		org.apache.struts.taglib.html.BaseFieldTag {

	// public static final String GET_VALUE_FROM_PARAMS =
	// "_GET_VALUE_FROM_PARAMS";

	/**
	 * 字段的名称
	 */
	protected String title;

	/**
	 * 格式化参数
	 */
	protected String formater;

	/**
	 * 初始化，可以常量;也可以是变量，如“DATE{TODAY}”;
	 */
	protected String initial;

	// /**
	// * 当输入域(特别是按钮)需求权限控制时,应配置此权限的编码
	// */
	// protected String privilegeId;

	/**
	 * 输入域是否是必添项
	 */
	protected boolean isRequired = false;

	/**
	 * 被格式化之前的值
	 */
	protected Object beforeFormatedValue;

	/**
	 * 域控制参数
	 */
	private String fieldController;

	private IFieldController fc;

	public int doStartTag() throws JspException {
		if (StringUtils.isNotEmpty(fieldController)) {
			ParamParser pp = ParamParser.getInstance(fieldController);
			String name = pp.getName();
			String params = pp.getParams();
			fc = FieldControllerFactory.getInstance().get(name);
			HttpServletRequest hsr = (HttpServletRequest) pageContext
					.getRequest();
			RequestEvent re = HttpRequestEvent.getInstance(hsr);
			if (!fc.isVisiable(re, params)) {
				// 不可见则跳过此输入域的显示()
				return SKIP_BODY;
			}
			if (!fc.isReadonly(re, params)) {
				setReadonly(true);
			}
		}

		// if (privilegeId != null) {
		// SystemConfigerature sc = SystemConfigerature.getInstance();
		// fieldController = (IFieldController) sc.readObject(
		// SystemConstants.FIELD_CONTROLLER, null);
		// if (fieldController == null) {
		// throw new JspException("定义了权限编码但未定义控制类");
		// }
		// if (!fieldController.canVisiable(pageContext.getRequest(),
		// privilegeId)) {
		// // 不可见则跳过此输入域的显示()
		// // return SKIP_BODY;
		// setStyleClass("");
		// } else if (!fieldController.isReadonly(pageContext.getRequest(),
		// privilegeId)) {
		// setReadonly(true);
		// }
		// }

		// 如果没有设置名称，则默认采用FormTag的属性
		if (this.name == null) {
			this.name = FormTag.BEAN_NAME;
		}

		// 此类计算值，而不是采用父类的值
		caculateValue();

		return super.doStartTag();
	} // /**

	// * 如果出现了异常并需要返回到前一页的情况下，需要重新读取值的方式。
	// */
	// protected void prepareValue(StringBuffer results) throws JspException {
	// ServletRequest req = pageContext.getRequest();
	// if (req.getAttribute("GET_VALUE_FROM_PARAMS") != null) {
	// results.append(" value='");
	// results.append(formatValue(req.getParameter(property)));
	// results.append("'");
	// } else {
	// results.append(" value=\"");
	// if (value != null) {
	// results.append(this.formatValue(value));
	//
	// } else if (redisplay || !"password".equals(type)) {
	// try {
	// Object value = TagUtils.getInstance().lookup(pageContext,
	// name, property, null);
	//
	// results.append(this.formatValue(value));
	// } catch (Throwable e) {
	// // 如果未找到指定值，则不进行处理
	// LogWriter.warning(e.getMessage());
	// }
	// }
	//
	// results.append('"');
	// // super.prepareValue(results);
	//
	// }
	// }

	/**
	 * 计算数值，计算后将父类中的value填写上
	 * 
	 * @return
	 * @throws JspException
	 */
	protected void caculateValue() throws JspException {
		try {
			ServletRequest req = pageContext.getRequest();
			if (req.getAttribute("GET_VALUE_FROM_PARAMS") != null) {
				beforeFormatedValue = req.getParameter(property);
			} else {
				beforeFormatedValue = TagUtils.getInstance().lookup(
						pageContext, name, property, null);
			}
		} catch (Throwable e) {
			// 如果未找到指定值，则不进行处理
			LogWriter.warn(e.getMessage());
		}
		this.value = myFormatValue(beforeFormatedValue);
	}

	/**
	 * 对参数进行格式化，根据参数的类型，得到参数的格式化方法。
	 * formater可以采用如下方式进行格式化：dic{nation},即使用名称为“nation”的字典表
	 */
	protected String myFormatValue(Object value) throws JspException {
		String ret = null;
//		LogWriter.debug("myFormatValue value", value);
		if (value != null) {
//			LogWriter.debug("myFormatValue value.getClass()", value.getClass());
			Formater ft = FormaterUtils.getInstance().get(value.getClass());
//			LogWriter.debug("ft", ft);
			if (ft != null) {
				ret = ft.format(value, formater);
			} else {
				ret = value.toString();
			}
		} else {
			// 使用Struts标签的格式化功能
			ret = "";
		}
//		LogWriter.debug("ret", ret);
		return ret;
	}

	public void release() {
		super.release();
		title = null;
		formater = null;
		initial = null;
		// privilegeId = null;
		isRequired = false;
		fieldController = null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormater() {
		return formater;
	}

	public void setFormater(String formater) {
		this.formater = formater;
	}

	public String getFieldController() {
		return fieldController;
	}

	public void setFieldController(String fieldController) {
		this.fieldController = fieldController;
	}

	// public String getPrivilegeId() {
	// return privilegeId;
	// }
	//
	// public void setPrivilegeId(String privilegeId) {
	// this.privilegeId = privilegeId;
	// }

}

