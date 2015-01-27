package com.xt.gt.ui.fsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xt.core.exception.SystemException;
import com.xt.gt.ui.fsp.filter.FilterGroup;
import com.xt.gt.ui.fsp.filter.FilterItem;

/**
 * 自动参数类，用于封装过滤、分页和排序等参数。
 * 
 * @author albert
 */
public class FspParameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -133181082782170698L;

	/**
	 * 分页参数
	 */
	private final Pagination pagination = new Pagination();

	/**
	 * 排序条件
	 */
	private final ArrayList<SortItem> sorts = new ArrayList<SortItem>(2);

	/**
	 * 过滤参数组
	 */
	private final FilterGroup filterGroup = new FilterGroup();

	/**
	 * 用户可自定义一些属性。
	 */
	private HashMap<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * 默认的构造函数
	 */
	public FspParameter() {
	}
	

	/**
	 * 返回过滤信息组
	 * 
	 * @return 不能为空的过滤信息组对象。
	 */
	public FilterGroup getFilterGroup() {
		return filterGroup;
	}

    public void setFilterGroup(FilterGroup _filterGroup) {
        if (_filterGroup == null) {
            return;
        }
        for (Iterator<FilterItem> fis = _filterGroup.getItems().iterator(); fis.hasNext(); ) {
            this.filterGroup.addFilterItem((FilterItem)fis.next().clone());
        }
	}

	/**
	 * 设置翻页的开始行号，此为代理方法。
	 * 
	 * @param startIndex
	 *            开始行号
	 */
	public void setStartIndex(int startIndex) {
		pagination.setStartIndex(startIndex);
	}


	/**
	 * 返回翻页信息
	 * 
	 * @return 翻页信息，不能为空。
	 */
	public Pagination getPagination() {
		return pagination;
	}
    
    public void setPagination(Pagination _pagination) {
        if (_pagination == null) {
            return;
        }
		pagination.setMaxRowCount(_pagination.getMaxRowCount());
        pagination.setName(_pagination.getName());
        pagination.setRowsPerPage(_pagination.getRowsPerPage());
        pagination.setStartIndex(_pagination.getStartIndex());
        pagination.setTotalCount(_pagination.getTotalCount());
	}

	/**
	 * 返回排序信息列表
	 * 
	 * @return 排序信息列表，不能为空。
	 */
	public Iterator<SortItem> getSorts() {
		return sorts.iterator();
	}

    /**
     * 同 addSorts 方法，此方法是为了符合 Bean 标准而设计。
     * @param sorts
     */
    public void setSorts(List<SortItem> sorts) {
        addSorts(sorts);
	}

	/**
	 * 增加一个排序选项，此为代理方法。
	 * 
	 * @param item
	 *            排序选项
	 */
	public void addSort(SortItem sort) {
		if (sort != null) {
			// 避免重复并依次排序
			sorts.remove(sort);
			// 后入的先排序
			sorts.add(0, sort);
		}
	}
	
	/**
	 * 增加多个排序选项，此为代理方法。(因为addSort有后进先出的特性，
	 * 所以在连续加入多个排序选项时存在倒序的问题)
	 * 
	 * @param item
	 *            排序选项
	 */
	public void addSorts(List<SortItem> _sorts) {
		if (sorts != null) {
			for (SortItem item : _sorts) {
				sorts.remove(item);
			}
			for (int i = _sorts.size() - 1; i >= 0; i--) {
				SortItem item = _sorts.get(i); 
				sorts.add(0, item);
			}
		}
	}

	/**
	 * 移除一个排序选项，此为代理方法。
	 * 
	 * @param item
	 *            排序选项
	 */
	public void removeSort(SortItem sort) {
		if (sort != null) {
			sorts.remove(sort);
		}
	}

	/**
	 * 增加一个过滤选项，此为代理方法。
	 * 
	 * @param item
	 *            过滤选项
	 */
	public void addFilterItem(FilterItem item) {
		filterGroup.addFilterItem(item);
	}
	
	/**
	 * 移除指定过滤选项，此为代理方法。
	 * 
	 * @param item
	 *            过滤选项
	 */
	public void removeFilterItem(FilterItem item) {
		filterGroup.removeFilterItem(item);
	}

	/**
	 * 返回指定名称的属性值。
	 * 
	 * @param name
	 *            属性名称
	 * @return 指定名称的属性值，如果不存在，则返回空。
	 */
	public Object getAttribute(String name) {
		if (name == null) {
			return null;
		}
		return this.attributes.get(name);
	}

	/**
	 * 设置指定名称的属性值。如果属性名称为空，则抛出 SystemException 异常。
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public void setAttribute(String name, Object value) {
		if (name == null) {
			throw new SystemException("属性名称不能为空。");
		}
		this.attributes.put(name, value);
	}

    public void setAttributes(Map<String, Object> attributes) {
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString()).append("[");
		strBld.append("pagination=").append(pagination).append(",");
		strBld.append("sorts=").append(sorts).append(",");
		strBld.append("filterGroup=").append(filterGroup).append(",");
		strBld.append("attributes=").append(attributes);
		strBld.append("]");
		return strBld.toString();
	}

}
