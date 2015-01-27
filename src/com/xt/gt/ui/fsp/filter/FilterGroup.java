package com.xt.gt.ui.fsp.filter;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import java.util.ArrayList;


/**
 * 过滤选项集合，用于定义过滤条件的集合，各选项之间存在顺序关系，且是“并”(and) 的关系。
 * 其和"And"的差别是，这个过滤组将去除重复的过滤选项。
 * @author albert
 */

public class FilterGroup extends AndFilterItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7648371654793456462L;



	public FilterGroup() {
	}


	/**
	 * 增加一个过滤选项，如果参数为空，则不进行任何处理。如果相同的选项以及存在，
	 * 则首先删除之，然后在其后进行追加。
	 * @param item
	 */
	public void addFilterItem(FilterItem item) {
		if (item != null) {
			// 避免重复并依次排序
			remove(item);
			and(item);
		}
	}
	
	/**
	 * 移除指定的过滤选项，如果参数为空，则不进行任何处理。
	 * @param item
	 */
	public void removeFilterItem(FilterItem item) {
		if (item != null) {
			remove(item);
		}
	}

    /**
     * 过滤项的集中设置，增加此参数主要是为了 JS 复制的实现。
     * 注意：此方法的限制比较多，不建议程序员直接使用此方法。
     * @param filterItems
     */
    public void setFilterItems(ArrayList filterItems) {
        if (filterItems != null) {
            for (Object fi : filterItems) {
                if (fi != null && !(fi instanceof FilterItem)) {
                    Converter converter = ConverterFactory.getInstance().getConverter(fi.getClass(), SimpleFilterItem.class);
                    fi = converter.convert(fi.getClass(), SimpleFilterItem.class, fi);
                }
                addFilterItem((FilterItem)fi);
            }
        }
    }

    /**
     * 返回所有的过滤项，增加此参数主要是为了 JS 复制的实现。
     */
    public ArrayList<FilterItem> getFilterItems() {
        return this.items;
    }
	
	/**
	 * 移除指定的过滤选项，如果参数为空，则不进行任何处理。
	 * @param item
	 */
	public void removeAll() {
		if (items != null) {
			items.clear();
		}
	}
    
//	/**
//	 * 判断此过滤组是否为空。
//	 * @return
//	 */
//	public boolean isEmpty() {
//		return super.isEmpty();
//	}
	
	/**
	 * 为过滤组中的所有过滤项设置相同的“QL”组装器。如果参数为空，系统无任何操作。
	 * @param qlBuilder “QL”组装器实例。
	 */
    @Override
	public void setQLBuilder (QLBuilder qlBuilder) {
		if (qlBuilder == null || items == null) {
			return;
		}
		for(FilterItem item : items) {
			item.setQLBuilder(qlBuilder);
		}
	}
	
}
