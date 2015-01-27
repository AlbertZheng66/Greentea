

package com.xt.gt.ui.fsp.filter;

import com.xt.core.exception.BadParameterException;
import org.apache.commons.lang.StringUtils;

/**
 * 使用简单的方式来构建一个过滤选项表达式的组合.
 * @author albert
 */
public class FilterItemBuilder {

    /**
     * 当前的过滤选项。
     */
    private FilterItem filterItem;

    public FilterItemBuilder() {
    }

    public FilterItemBuilder (FilterItem filterItem) {
        this.filterItem = filterItem;
    }

    /**
     * 构建一个简单类型（SimpleFilterItem）的过滤选项。
     * @param name 过滤选项的名称，不能为空。
     * @param type 过滤选项的类型，不能为空。
     * @param value 过滤选项的值
     */
    public FilterItemBuilder (String name, FilterType type, Object value) {
        assertSimpleFilterItem(name, type);
        this.filterItem = new SimpleFilterItem(name, type, value);
    }

    static private void assertSimpleFilterItem(String name, FilterType type) {
        if (StringUtils.isEmpty(name) || type == null) {
            throw new BadParameterException("过滤选项的名称和类型不能为空。");
        }
    }

    /**
     * 构建一个简单类型（SimpleFilterItem）的过滤选项。
     * @param name 过滤选项的名称，不能为空。
     * @param type 过滤选项的类型，不能为空。
     * @param value 过滤选项的值
     */
    static public FilterItemBuilder createSimpleFilterItem(String name, FilterType type, Object value) {
        assertSimpleFilterItem(name, type);
        FilterItemBuilder fib = new FilterItemBuilder(name, type, value);
        return fib;
    }

    /**
     * 将两个过滤选项构建器里已经组建的过滤选项进行“AND”操作。
     * @param other 如果选项构建器为空，或者构建器的过滤选项为空，则不进行任何处理。
     * @return 当前对象
     */
     public FilterItemBuilder and(FilterItemBuilder other) {
        if (other == null || other.filterItem == null) {
            return this;
        }
        return and(other.filterItem);
    }

    /**
     * 首先创建一个简单类型的构建器，然后和当前过滤选项进行“AND”操作。
     * @param name 过滤选项的名称，不能为空。
     * @param type 过滤选项的类型，不能为空。
     * @param value 过滤选项的值
     */
    public FilterItemBuilder and(String name, FilterType type, Object value) {
        assertSimpleFilterItem(name, type);
        SimpleFilterItem _filterItem = new SimpleFilterItem(name, type, value);
        return and(_filterItem);
    }

    /**
     * 在当前过滤选项上和参数传入的过滤选项进行“AND”操作。
     * @param _filterItem 被合并的过滤选项，如果为空，不进行任何处理。
     */
    public FilterItemBuilder and(FilterItem _filterItem) {
        if (_filterItem == null) {
            return this;
        }
        if (this.filterItem == null) {
            this.filterItem = _filterItem;
        } else if (filterItem instanceof AndFilterItem){
            AndFilterItem andFilterItem = (AndFilterItem)this.filterItem;
            andFilterItem.and(_filterItem);
        } else {
            AndFilterItem andFilterItem = new AndFilterItem(this.filterItem);
            andFilterItem.and(_filterItem);
            this.filterItem = andFilterItem;
        }
        return this;
    }

    /**
     * 将两个过滤选项构建器里已经组建的过滤选项进行“OR”操作。
     * @param other 如果选项构建器为空，或者构建器的过滤选项为空，则不进行任何处理。
     * @return 当前对象
     */
     public FilterItemBuilder or(FilterItemBuilder other) {
        if (other == null || other.filterItem == null) {
            return this;
        }
        return or(other.filterItem);
    }

    /**
     * 首先创建一个简单类型的构建器，然后和当前过滤选项进行“OR”操作。
     * @param name 过滤选项的名称，不能为空。
     * @param type 过滤选项的类型，不能为空。
     * @param value 过滤选项的值
     */
    public FilterItemBuilder or(String name, FilterType type, Object value) {
        assertSimpleFilterItem(name, type);
        SimpleFilterItem _filterItem = new SimpleFilterItem(name, type, value);
        return or(_filterItem);
    }

    /**
     * 在当前过滤选项上和参数传入的过滤选项进行“OR”操作。
     * @param _filterItem 被合并的过滤选项，如果为空，不进行任何处理。
     */
    public FilterItemBuilder or(FilterItem _filterItem) {
        if (_filterItem == null) {
            return this;
        }
        if (this.filterItem == null) {
            this.filterItem = _filterItem;
        } else if (filterItem instanceof OrFilterItem){
            OrFilterItem orFilterItem = (OrFilterItem)this.filterItem;
            orFilterItem.or(_filterItem);
        } else {
            OrFilterItem orFilterItem = new OrFilterItem(this.filterItem);
            orFilterItem.or(_filterItem);
            this.filterItem = orFilterItem;
        }
        return this;
    }

    /**
     * 返回当前的过滤选项。
     * @return 过滤选项实例
     */
    public FilterItem getFilterItem() {
        return filterItem;
    }
}
