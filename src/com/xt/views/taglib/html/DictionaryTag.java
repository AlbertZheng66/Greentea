package com.xt.views.taglib.html;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.dic.Dictionary;
import com.xt.core.utils.dic.Item;
import com.xt.core.utils.dic.DictionaryService;

public class DictionaryTag extends BaseFieldTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1234365451763996866L;

	/**
	 * 初始值
	 */
	private String initial;

	/**
	 * 是否允许多选
	 */
	private boolean multiple = false;

	/**
	 * 列表框的宽度
	 */
	private int listSize = 1;

	/**
	 * 展示字典的类型：下拉列表框（combobox，默认），列表（查询时多用，list），
	 * 复选框（checkbox），单选钮（radio），Tree等控件。
	 */
	private String type = "combobox";
	
	/**
	 * 是否允许为空
	 */
	private boolean nullable = true;
	
	/**
	 * 当值未空时，页面应该显示的标题
	 */
	private String nullTitle;
	
	/**
	 * 字典名称
	 */
	private String dicName;
	
	private boolean disabled;
	
	/**
	 * 参数标签的参数,如果不为空,表示这个标签是一个参数
	 */
	private String param;

	public int doStartTag() throws JspException {
		DictionaryService ds = DictionaryService.getInstnace();
		Dictionary dictionary = null;
		if (param == null) {
			dictionary = ds.get(dicName);
		} else {
			dictionary = ds.get(dicName, param);
		}
		
		
		if (dictionary == null) {
			throw new JspException("字典[" + dicName + "]不存在，请查看字典的定义");
		}

		Object propertyValue = null;
		try {
			propertyValue = TagUtils.getInstance().lookup(pageContext, name, property, null);
			if (propertyValue == null) {
				propertyValue = pageContext.getRequest().getAttribute(property);
			}
		} catch (Throwable e) {
			// 如果未找到任何值，则不进行任何处理
		}
		LogWriter.debug("propertyValue", propertyValue);

		if ("combobox".equals(type)) {
            listSize = 1; // 下拉列表框的尺寸只能是一，也只能是单选
    		multiple = false;
			TagUtils.getInstance().write(pageContext,
					renderSelectElement(dictionary, propertyValue));
		} else if ("list".equals(type)) {
            TagUtils.getInstance().write(pageContext,
            		renderSelectElement(dictionary, propertyValue));
		} else if ("checkbox".equals(type) || "radio".equals(type)) {
			TagUtils.getInstance().write(pageContext,
					renderCheckBoxOrRadio(dictionary, propertyValue));
		} else {
			throw new JspException("未知的字典标签[" + type + "]类型");
		}

		return (SKIP_BODY);
	}


	private String renderCheckBoxOrRadio(Dictionary dic, Object propertyValue)
	    throws JspException {
		
		// 显示的行数
//		int rows = 1;
		int rowCount = dic.getItems().length;
		
        //多行显示
//		if (listSize > 1) {
//			rows = rowCount / listSize + 1;	
//		}
		
		int index = rowCount;
		StringBuffer results = new StringBuffer();
		if (nullable && "radio".equals(type)) {
			Item nullItem = new Item();
			nullItem.setValue("");
			if (nullTitle != null) {
			    nullItem.setTitle(nullTitle);
			}
			renderInputElement(results, nullItem, propertyValue);
			if (--index == 0) {
				results.append("<br>");
				index = rowCount;
			}
		}
		for (int i = 0; i < dic.getItems().length; i++) {
			Item dicItem = dic.getItems()[i];

			// 无效选项不允许选择
			if (!dicItem.isValidate()) {
				continue;
			}
			
			renderInputElement(results, dicItem, propertyValue);
			if (--index == 0) {
				results.append("<br>");
				index = rowCount;
			}
		}
		return results.toString();
	}

	/**
	 * Renders a fully formed &lt;input&gt; element.
	 * 
	 * @throws JspException
	 * @since Struts 1.2
	 */
	protected void renderInputElement(StringBuffer results,
			Item dicItem, Object propertyValue) throws JspException {
		
		
		results.append("<input");
		if (disabled) {
			results.append(" disabled=\"disabled\"");
		}

		LogWriter.debug("dicItem.getTitle()", dicItem.getTitle());
		LogWriter.debug("dicItem.getValue()", dicItem.getValue());
		
//		 可以使用初始值和属性值进行初始化
		if (equals(dicItem.getValue(), propertyValue) 
				|| (propertyValue == null && initial != null
						&& equals(dicItem.getValue(), initial))) {
			results.append(" checked=\"checked\"");
		}
		
		prepareAttribute(results, "type", type);
		prepareAttribute(results, "name", property);
		prepareAttribute(results, "accesskey", getAccesskey());
		prepareAttribute(results, "accept", getAccept());
		prepareAttribute(results, "maxlength", getMaxlength());
		prepareAttribute(results, "size", getCols());
		prepareAttribute(results, "tabindex", getTabindex());
		prepareAttribute(results, "value", dicItem.getValue());
		prepareAttribute(results, "id", getStyleId() == null ? property : getStyleId());
		
		// 不起作用
		if (getReadonly()) {
			prepareAttribute(results, "readonly", "readonly");
		}
		results.append(prepareEventHandlers());
		results.append(prepareStyles());
		prepareOtherAttributes(results);
		results.append(getElementClose());
		results.append(dicItem.getTitle());
	}
	
	/**
	 * 判断字典值和属性值是否相等。
	 * @param disItemValue
	 * @param value 注意：基类里还有一个“value”属性
	 * @return
	 */
	private boolean equals (Object dicItemValue, Object propertyValue) {
		if (propertyValue instanceof List) {
			List list = (List)propertyValue;
			return (list.contains(dicItemValue));
		}
		return dicItemValue.equals(propertyValue);
	}

	/**
	 * Generate an HTML %lt;option&gt; element.
	 * 
	 * @throws JspException
	 * @since Struts 1.1
	 */
	protected String renderSelectElement(Dictionary dic, Object propertyValue) 
	    throws JspException {
		StringBuffer results = new StringBuffer("<SELECT ");

		prepareAttribute(results, "size", listSize);
		prepareAttribute(results, "name", this.property);
		if (disabled) {
			results.append(" disabled=\"disabled\"");
		}
		prepareAttribute(results, "id", getStyleId() == null ? property : getStyleId());
		
		results.append(prepareEventHandlers());
		results.append(prepareStyles());
		results.append(">");
		
		//创建一个空值选项
		if (nullable) {
			results.append("<option value='' >");

			if (nullTitle != null) {
			    results.append(nullTitle);
			} else {
				results.append("  ");
			}
			results.append("</option>");
		}
		
		for (int i = 0; i < dic.getItems().length; i++) {
			Item dicItem = dic.getItems()[i];

			// 无效选项不允许选择
			if (!dicItem.isValidate()) {
				continue;
			}

			results.append("<option value=\"");

			results.append(dicItem.getValue());
			results.append("\"");
			
			// 可以使用初始值和属性值进行初始化
			if (equals(dicItem.getValue(), propertyValue) 
					|| (propertyValue == null && initial != null
							&& equals(dicItem.getValue(), initial))) {
				results.append(" selected=\"selected\"");
			}
			
			if (multiple) {
				results.append(" multiple=\"multiple\"");
			}

			results.append(">");

			results.append(dicItem.getTitle());

			results.append("</option>");
		}

		results.append("</SELECT>");

		return results.toString();
	}


	public String getInitial() {
		return initial;
	}


	public void setInitial(String initail) {
		this.initial = initail;
	}


	public boolean isMultiple() {
		return multiple;
	}


	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

    

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getListSize() {
		return listSize;
	}


	public void setListSize(int listSize) {
		this.listSize = listSize;
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


	public String getDicName() {
		return dicName;
	}


	public void setDicName(String dicName) {
		this.dicName = dicName;
	}


	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	public String getParam() {
		return param;
	}


	public void setParam(String param) {
		this.param = param;
	}
	
	
	
}
