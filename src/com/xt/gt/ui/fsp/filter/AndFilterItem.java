package com.xt.gt.ui.fsp.filter;

import java.util.ArrayList;
import java.util.List;


public class AndFilterItem extends AbstractFilterItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3750040516464989517L;

	/**
	 * 并（and）语句的连接符
	 */
	public static final String AND_STATEMENT_PARAMETER = " AND ";
	
	protected ArrayList<FilterItem> items = new ArrayList<FilterItem>(5);
	
	public AndFilterItem() {
	}


	public AndFilterItem(FilterItem item) {
		if (item != null) {
			items.add(item);
		}
	}
	
	public ArrayList<FilterItem> getItems () {
		return items;
	}

	public AndFilterItem and(FilterItem item) {
		if (item != null) {
			items.add(item);
		}
		return this;
	}
	
	public AndFilterItem remove(FilterItem item) {
		if (item != null) {
			items.remove(item);
		}
		return this;
	}
	
	public Object clone() {
		AndFilterItem afi = (AndFilterItem) super.clone();
		if (items != null) {
			afi.items = new ArrayList<FilterItem>(items.size());
			for(FilterItem fi : items) {
				afi.items.add((FilterItem) fi.clone());
			}
		}
		return afi;
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}

    @Override
	public void setQLBuilder(QLBuilder qlBuilder) {
		this.qlBuilder = qlBuilder;
		if (this.items != null) {
			for(FilterItem item : items) {
				item.setQLBuilder(qlBuilder);
			}
		}
	}
	
	public boolean match (Object obj) {
		if (items.isEmpty()) {
			return true;
		}
		for (FilterItem item : items) {
			if (!item.match(obj)) {
				return false;
			}
		}
		return true;
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
		AndFilterItem other = (AndFilterItem) obj;
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
		return AND_STATEMENT_PARAMETER;
	}
	
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString()).append("[");
		boolean flag = false;
		for (int i = 0; i < items.size(); i++) {
			FilterItem item = items.get(i);
			if (flag) {
				strBld.append(",").append(AND_STATEMENT_PARAMETER);
			}
			strBld.append(item);
			flag = true;
		}
		
		strBld.append("]");
		return strBld.toString();
	}


	public boolean isIdentical(FilterItem item) {
		if (!equals(item)) {
			return false;
		}
		AndFilterItem other = (AndFilterItem)item;
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
