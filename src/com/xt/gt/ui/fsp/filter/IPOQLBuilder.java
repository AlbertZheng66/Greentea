package com.xt.gt.ui.fsp.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.SystemException;

public class IPOQLBuilder implements QLBuilder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8303448014267354149L;

	public IPOQLBuilder() {
		
	}

	public String getQLString(FilterItem filterItem, String alias) {
		if (filterItem instanceof SimpleFilterItem) {
			return createSimpleStatement((SimpleFilterItem)filterItem, alias);
		} else if (filterItem instanceof AndFilterItem) {
			return createAndStatement((AndFilterItem)filterItem, alias);
		} else if (filterItem instanceof OrFilterItem) {
			return createOrStatement((OrFilterItem)filterItem, alias);
		}
		throw new SystemException(String.format("不能识别的过滤项类型[%s]。", filterItem));
	}

	private String createAndStatement(AndFilterItem filterItem, String alias) {
		List<FilterItem> items = ( filterItem).getItems();
		StringBuilder strBld = new StringBuilder("(");
		if (items == null || items.size() == 0) {
			strBld.append(" 1=1 ");
		} else {
			boolean flag = false; // 多于一个有效语句时，此标记为真（true）。
			for (int i = 0; i < items.size(); i++) {
				FilterItem item = items.get(i);
				if (item == null) {
					continue;
				}
				String qlString = item.toQlString(alias);
				if (StringUtils.isEmpty(qlString)) {
					continue;
				}
				if (flag) {
					strBld.append(AndFilterItem.AND_STATEMENT_PARAMETER);
				}
				strBld.append(qlString);
				flag = true;
			}
		}
		strBld.append(")");
		return strBld.toString();
	}

	private String createSimpleStatement(SimpleFilterItem filterItem, String alias) {
		// 构建别名
		StringBuilder strBld = new StringBuilder();

		if (StringUtils.isNotEmpty(alias)) {
			strBld.append(alias).append(".");
		}
		strBld.append(filterItem.getName());
		String columnName = strBld.toString();
		FilterType type = filterItem.getType();
		if (type == FilterType.EQUALS) {
			strBld.append("= ?");
		} else if (type == FilterType.CONTAINS) {
			strBld.append(" LIKE  '%'||?||'%'");
		} else if (type == FilterType.STARTS_WITH) {
			strBld.append(" LIKE ?||'%'");
		} else if (type == FilterType.ENDS_WITH) {
			strBld.append(" LIKE '%'||?");
		} else if (type == FilterType.IN) {
			List values = ((SimpleFilterItem) filterItem).getValues();
			if (values == null || values.isEmpty()) {
				throw new SystemException(String.format(
						"过滤选项[%s]的参数个数须大约等于1。", FilterType.IN.name()));
			}
			String[] questions = new String[values.size()];
			Arrays.fill(questions, "?");
			strBld.append(" IN (").append(StringUtils.join(questions, ","))
					.append(")");
		} else if (type == FilterType.BETWEEN) {
			strBld.append(" BETWEEN ? AND ?");
		} else if (type == FilterType.GREAT_THAN) {
			strBld.append(" > ?");
		} else if (type == FilterType.GREAT_EQUAL_THAN) {
			strBld.append(" >= ?");
		} else if (type == FilterType.LESS_EQUAL_THAN) {
			strBld.append(" <= ?");
		} else if (type == FilterType.LESS_THAN) {
			strBld.append(" <= ?");
		} else if (type == FilterType.IS_NULL) {
			strBld.append(" = NULL");
		} else if (type == FilterType.IS_EMPTY) {
			strBld.insert(0, "(");
			strBld.append(" = NULL OR ").append(columnName).append("='')");
		} else {
			strBld.append(type.name()).append(" ? ");
		}
		return strBld.toString();
	}
	
	public String createOrStatement(OrFilterItem filterItem, String alias) {
		List<FilterItem> items = filterItem.getItems();
		StringBuilder strBld = new StringBuilder("(");
		if (items == null || items.size() == 0) {
			strBld.append(" 1=1 ");
		} else {
			boolean flag = false; // 多于一个有效语句时，此标记为真（true）。
			for (int i = 0; i < items.size(); i++) {
				FilterItem item = items.get(i);
				if (item == null) {
					continue;
				}
				String qlString = item.toQlString(alias);
				if (StringUtils.isEmpty(qlString)) {
					continue;
				}
				if (flag) {
					strBld.append(OrFilterItem.OR_STATEMENT_PARAMETER);
				}
				strBld.append(qlString);
				flag = true;
			}
		}
		strBld.append(")");
		return strBld.toString();
	}

	public Object[] getParams(FilterItem filterItem) {
		if (filterItem instanceof SimpleFilterItem) {
			return ((SimpleFilterItem)filterItem).getValues().toArray();
		} else if (filterItem instanceof AndFilterItem 
				|| filterItem instanceof OrFilterItem) {
			List<FilterItem> items = null;
			if (filterItem instanceof AndFilterItem) {
				items = ((AndFilterItem)filterItem).getItems();
			} else {
				items = ((OrFilterItem)filterItem).getItems();
			}
			if (items == null) {
				return null;
			}
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
		throw new SystemException(String.format("不能识别的过滤项类型[%s]。", filterItem));
	}

}
