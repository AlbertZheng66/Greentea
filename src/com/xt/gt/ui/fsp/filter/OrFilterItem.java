package com.xt.gt.ui.fsp.filter;

import java.util.ArrayList;
import java.util.List;


public class OrFilterItem extends AbstractFilterItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8318928062400389042L;

	/**
	 * 或（or）语句的连接符
	 */
	public static final String OR_STATEMENT_PARAMETER = " OR ";

	private List<FilterItem> items = new ArrayList<FilterItem>(2);

	public OrFilterItem(FilterItem item) {
		if (item != null) {
			items.add(item);
		}
	}

	public OrFilterItem or(FilterItem item) {
		if (item != null) {
			items.add(item);
		}
		return this;
	}
	
	public Object clone() {
		OrFilterItem ofi = (OrFilterItem) super.clone();
		
		if (items != null) {
			ofi.items = new ArrayList<FilterItem>();
			for(FilterItem fi : items) {
				ofi.items.add((FilterItem) fi.clone());
			}
		}
		return ofi;
	}
	
	public void setQLBuilder(QLBuilder qlBuilder) {
		this.qlBuilder = qlBuilder;
		if (this.items != null) {
			for(FilterItem item : items) {
				item.setQLBuilder(qlBuilder);
			}
		}
	}

	

	public Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		for(FilterItem item : items) {
			if  (item != null && item.getParameters() != null) {
				for (int i = 0; i < item.getParameters().length; i++) {
					params.add(item.getParameters()[i]);
				}
			}
		}
		return params.toArray(new Object[params.size()]);
	}
	
	public boolean match (Object obj) {
		if (items.isEmpty()) {
			return true;
		}
		for (FilterItem item : items) {
			if (item.match(obj)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		OrFilterItem other = (OrFilterItem) obj;
		if (items == null && other.items == null) {
			return true;
		}
		// 选项顺序要求是无关的
		if (items == null && other.items != null || other.items == null && items != null) {
			return false;
		} else {
			for(FilterItem childItem : items) {
				int index = other.items.indexOf(childItem);
				if (index < 0) {
					return false;
				}
				FilterItem otherFilterItem = other.items.get(index);
				if (!childItem.equals(otherFilterItem)) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public String getName() {
		return OR_STATEMENT_PARAMETER;
	}
	
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString()).append("[");
		boolean flag = false;
		for (int i = 0; i < items.size(); i++) {
			FilterItem item = items.get(i);
			if (flag) {
				strBld.append(",").append(OR_STATEMENT_PARAMETER);
			}
			strBld.append(item);
			flag = true;
		}
		
		strBld.append("]");
		return strBld.toString();
	}

	public List<FilterItem> getItems() {
		return items;
	}

	public boolean isIdentical(FilterItem item) {
		if (!equals(item)) {
			return false;
		}
		OrFilterItem other = (OrFilterItem)item;
		if (items == null && other.items == null) {
			return true;
		}
		if ((items == null && other.items != null) 
				|| (items != null && other.items == null)
				|| (items.size() != other.items.size())) {
			return false;
		}
		// 选项顺序无关的
		for(FilterItem childItem : items) {
			int index = other.items.indexOf(childItem);
			if (index < 0) {
				return false;
			}
			FilterItem otherFilterItem = other.items.get(index);
			if (!childItem.isIdentical(otherFilterItem)) {
				return false;
			}
		}
		
		return true;
	}
	
}
