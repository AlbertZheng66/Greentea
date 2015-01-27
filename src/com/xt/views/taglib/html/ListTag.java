package com.xt.views.taglib.html;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.utils.BeanHelper;

public class ListTag extends BaseFieldTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5020272665681793700L;

	private String list;

	/**
	 * 显示列表的值
	 */
	private Object listValue;

	private boolean nullable;

	private String nullTitle;

	private String valueProperty;

	private String titleProperty;

	/**
	 * 被选择的属性值
	 */
	private Object propertyValue;

	public ListTag() {
		super();
	}

	public int doStartTag() throws JspException {
		propertyValue = TagUtils.getInstance().lookup(pageContext, name,
				property, null);
		listValue = TagUtils.getInstance()
				.lookup(pageContext, name, list, null);
		if (listValue == null) {
			listValue = TagUtils.getInstance().lookup(pageContext, list, null);
		}
		if (listValue == null) {
			throw new JspException("不能得到对象[" + name + "]的属性[" + list + "]值。");
		}

		TagUtils.getInstance().write(pageContext, renderSelectCtl());

		return (SKIP_BODY);

	}

	protected String renderSelectCtl() throws JspException {
		StringBuffer results = new StringBuffer("<SELECT ");

		prepareAttribute(results, "size", 1);
		prepareAttribute(results, "name", property);
		prepareAttribute(results, "id", getStyleId() == null ? property
				: getStyleId());

		results.append(prepareEventHandlers());
		results.append(prepareStyles());
		results.append(">");

		// 创建一个空值选项
		if (nullable) {
			results.append("<option value='' >");

			if (nullTitle != null) {
				results.append(nullTitle);
			} else {
				results.append("  ");
			}
			results.append("</option>");
		}

		if (listValue instanceof Collection) {
			for (Iterator iter = ((Collection) listValue).iterator(); iter
					.hasNext();) {
				Object element = iter.next();
				createOption(results, element);
			}
		}

		results.append("</SELECT>");

		return results.toString();
	}

	private void createOption(StringBuffer option, Object value) {
		Object optionValue = BeanHelper.getProperty(value, valueProperty);
		option.append("<option value='");
		option.append(optionValue);
		option.append("'");

		if (propertyValue != null && propertyValue.equals(optionValue)) {
			option.append(" selected='selected' ");
		}

		// // 可以使用初始值和属性值进行初始化
		// if (option.getValue().equals(initial)
		// || dicItem.getValue().equals(value)) {
		// results.append(" selected=\"selected\"");
		// }

		option.append(">");

		option.append(BeanHelper.getProperty(value, titleProperty));

		option.append("</option>");
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getNullTitle() {
		return nullTitle;
	}

	public void setNullTitle(String nullTitle) {
		this.nullTitle = nullTitle;
	}

	public String getTitleProperty() {
		return titleProperty;
	}

	public void setTitleProperty(String titleProperty) {
		this.titleProperty = titleProperty;
	}

	public String getValueProperty() {
		return valueProperty;
	}

	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

}
