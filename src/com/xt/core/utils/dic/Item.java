package com.xt.core.utils.dic;

import java.io.Serializable;

public class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5854623413584872027L;

	/**
	 * 字典的标题，描述性信息
	 */
	private String title;

	/**
	 * 字典值，唯一的索引值
	 */
	private Object value;

	/**
	 * 在字典中的顺序，排序时使用
	 */
	private int index;

	/**
	 * 目前此值是否生效(如果此值无效，在选择时不可选，而在显示时可显示) 默认为：有效
	 */
	private boolean validate = true;

	// /**
	// * 显示的形式，在下拉列表框或者别的控件中显示的标题
	// */
	// private String display;

	public Item() {

	}

	// public String getDisplay() {
	// return display;
	// }
	//
	// public void setDisplay(String display) {
	// this.display = display;
	// }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder(super.toString());
		strBld.append("[");
		strBld.append("title=").append(title).append(",");
		strBld.append("value=").append(value).append(",");
		strBld.append("index=").append(index).append(",");
		strBld.append("validate=").append(validate);
		strBld.append("]");
		return strBld.toString();
	}

}
