/*
 * $Header: f:/cvsroot/core/src/com/xt/views/taglib/html/TextTag.java,v 1.2 2006/09/10 11:36:10 zhengwei Exp $
 * $Revision: 1.2 $
 * $Date: 2006/09/10 11:36:10 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.val.IValidator;
import com.xt.core.val.RequiredValidator;
import com.xt.core.val.ValidatorFactory;

/**
 * 
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 输出一个错误提示框。
 * </p>
 * <span id='count-iewin' class='count'></span>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-11-1
 */

public class TextTag extends BaseFieldTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2897995562766798689L;

	/**
	 * 如果文本输入域是必添项，则此字符串表示它的style属性的默认字符串。
	 */
	private static String requiredInputTextStyle;

	/**
	 * 如果文本输入域不是必添项，则此字符串表示它的style属性的默认字符串。
	 */
	private static String optionalInputTextStyle;

	/**
	 * 只读文本输入域的style属性的默认字符串。
	 */
	private static String readOnlyInputTextStyle;

	/**
	 * 用户自定义的校验列表.
	 */
	protected String validator;

	/**
	 * 此输入域是否是必添项
	 */
	private boolean required = false;

	private transient IValidator[] validators = null;

	/**
	 * 用于校验的Title属性
	 */
	private String validateTitle;

	private boolean showTooltip = true;

	static {
		TagProperty tp = TagProperty.newInstance();

		requiredInputTextStyle = tp.getProperty("input_required_css");
		optionalInputTextStyle = tp.getProperty("input_optional_css");
		readOnlyInputTextStyle = tp.getProperty("input_readonly_css");
	}

	/**
	 * Construct a new instance of this tag.
	 */
	public TextTag() {
		super();
		this.type = "text";
		doReadonly = true;
	}

	public int doStartTag() throws JspException {
		//
		required = false;
		if (StringUtils.isNotEmpty(validator)) {
			ValidatorFactory factory = new ValidatorFactory();
			validators = factory.getValidators(validator);
			if (validators != null) {
				for (int i = 0; i < validators.length; i++) {
					IValidator valid = validators[i];
					if (valid instanceof RequiredValidator) {
						required = true;
						break;
					}
				}
			}
		}

		// 设置控件的Id默认为的属性名称
		if (showTooltip && this.getStyleId() == null) {
			this.setStyleId(this.property);
		}

		validateTitle = this.title;

		// 如果未定义自定义的提示，则显示默认的title
		if (!showTooltip) {
			this.title = null;
		}

		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		//
		release();
		return super.doEndTag();
	}

	protected void prepareOtherAttributes(StringBuffer handlers) {
		if (StringUtils.isNotBlank(validator)) {
			// 使用用户自定义校验
			prepareAttribute(handlers, "alt", createValidateStr());

		}

	}

	/**
	 * 组建校验表达式。多个表达式之间以”;;“分割
	 * 
	 * @return
	 */
	protected String createValidateStr() {
		if (validators == null) {
			return "";
		}

		StringBuilder altStr = null;
		// 用户自定义了校验也一定要定义域的标签
		if (validateTitle == null) {
			LogWriter.error("定义的校验器[" + validator + "]的同时要定义”title“属性!");
		}

		altStr = new StringBuilder();

		for (int i = 0; i < validators.length; i++) {
			IValidator valid = validators[i];

			// 多个表达式之间以”;;“分割
			if (i > 0) {
				altStr.append(";;");
			}

			String validteStr = valid.getRegExpStr(IValidator.JAVA_SCRIPT);
			if (validteStr != null) {
				altStr.append(validteStr);
				String titleStr = valid.getErrorMessage().replace("{title}",
						validateTitle);
				altStr.append(";").append(titleStr);
			}
		}
		return altStr.toString();
	}

	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}

	protected String prepareStyles() throws JspException {
		if (getReadonly()) {
			setStyleClass(readOnlyInputTextStyle);
		} else {
			if (required) {
				setStyleClass(requiredInputTextStyle);
			} else {
				setStyleClass(optionalInputTextStyle);
			}
		}
		return super.prepareStyles();
	}

	// /**
	// * 根据是否存在样式单来决定style样式。
	// *
	// * @param style
	// * @return
	// */
	// private String createStyle(String style) {
	// StringBuffer ret = new StringBuffer();
	// if (getStyle() != null) {
	// // 已有样式单的情况
	// ret.append(getStyle()).append(style);
	// } else {
	// ret.append(style);
	// }
	// return ret.toString();
	// }

	public void release() {
		super.release();
		validator = null;
		required = false;
		validator = null;
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

}
