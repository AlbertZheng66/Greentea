package com.xt.views.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.dic.Dictionary;
import com.xt.core.utils.dic.Item;
import com.xt.core.utils.dic.DictionaryService;

/**
 * 显示不可编辑的Label信息。
 * <p>
 * Title: XT框架-GreenTea标签库
 * </p>
 * <p>
 * Description: 显示一个不可编辑也不必提交的信心。 输出的标签格式为<div classStyle=""> value </div>
 * ，值的读取 方式与Text标签相同。
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
public class LabelTag extends BaseFieldTag {

	/**
	 * 属性名称，如果属性名称不为空，则从属性中取值。
	 */
	private String attr;

	/**
	 * 用于显示的标题，必添项，如果包含“${value}”变量，则将其变量替换为属性的值。
	 */
	private String title;

	/**
	 * 字典表名称
	 */
	private String dictionaryName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7321541872012402728L;

	/**
	 * 表示字典的值是多值，而非单值
	 */
	private boolean multi = false;

	/**
	 * 字典的值是多值时使用的分隔符
	 */
	private final static String MULTI_SEP = ",";

	public LabelTag() {
		super();
		this.property = "<null />"; // 标准标签中这个属性不能为空，赋予一个模拟值
	}

	/**
	 * 显示一个<div> 控件标签.
	 * 
	 * @throws JspException
	 * @since Struts 1.2
	 */
	protected String renderInputElement() throws JspException {
		Object propertyValue = null;
		if (attr != null) {
			propertyValue = pageContext.getRequest().getAttribute(attr);
		} else if (property != null) {
			propertyValue = TagUtils.getInstance().lookup(pageContext, name,
					property, null);
			// LogWriter.debug("propertyValue", propertyValue);
			// LogWriter.debug("dictionaryName", dictionaryName);
			if (dictionaryName != null && propertyValue != null) {
//				try {
					DictionaryService ds = DictionaryService.getInstnace();
					Dictionary dic = ds.get(dictionaryName);
					if (dic == null) {
						throw new JspException("未发现字典[" + dictionaryName
								+ "]的定义！");
					}
					if (multi) {
						// 字典是多值的情况
						if (propertyValue != null && propertyValue instanceof String) {
							String[] segs = ((String)propertyValue).split(MULTI_SEP);
						    StringBuilder strBld = new StringBuilder();
						    for (int i = 0; i < segs.length; i++) {
							    strBld.append(getDicTitle(segs[i], dic)).append("&nbsp;");
								
							}
						    propertyValue = strBld.toString();
						}

					} else {
						// 字典是单值的情况
						propertyValue = getDicTitle(propertyValue, dic);
					}
//				} catch (RuntimeException e) {
//					// 只记录日志不处理错误
//					LogWriter.error("转换字典表[" + dictionaryName + "]时出现错误！", e);
//				}
			} else {
				propertyValue = super.myFormatValue(propertyValue);
			}
		} else {
			throw new JspException("属性值[attr]和[property]不能同时为空！");
		}
		// LogWriter.debug("LabelTag propertyValue", propertyValue);

		String formatedValue = (propertyValue == null ? "" : propertyValue
				.toString());

		// 替换变量
		if (title != null) {
			formatedValue = title.replaceAll("\\$\\{value\\}", formatedValue);
		}
		return formatedValue;
	}

	private String getDicTitle(Object dicValue, Dictionary dic) {
		String title = null;
		Item item = dic.getOption(dicValue);
		if (item != null) {
			title = item.getTitle();
		}
		return title;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public boolean isMulti() {
		return multi;
	}

	public void setMulti(boolean multi) {
		this.multi = multi;
	}

}
