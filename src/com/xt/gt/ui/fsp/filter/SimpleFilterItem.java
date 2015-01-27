package com.xt.gt.ui.fsp.filter;

import com.xt.core.exception.SystemException;
import com.xt.core.utils.BeanHelper;
import java.util.ArrayList;

/**
 * 
 * @author albert
 */
public class SimpleFilterItem extends AbstractFilterItem {

    /**
     *
     */
    private static final long serialVersionUID = -3232331355786605309L;
    /**
     * 名称，可以是任意名称，如字段名、属性名，等等
     */
    private String name;
    /**
     * 类型
     */
    private FilterType type = FilterType.STARTS_WITH;
    private FilterComparator customizedFilterComparator = null;
    /**
     * 操作符的名称(默认为类型的名称)，在自定义的情况下，用户可以指定此名称。
     */
    private String operatorName = type.name();
    /**
     * 对于需要多条件的过滤选项（如：between、in等），可使用此数组值。
     */
    private Object[] values;
    /**
     * 如果属性是字符串，比较时是否忽略大小写，默认为 false。
     */
    private boolean ignoreCase = false;

    public SimpleFilterItem() {
        this.name = null;
    }

    public SimpleFilterItem(String name) {
        this.name = name;
    }

    public SimpleFilterItem(String name, FilterType type, Object value) {
        this.name = name;
        setType(type);
        this.values = new Object[1];
        this.values[0] = value;
    }

    public SimpleFilterItem(String name, FilterType type, Object[] values) {
        this.name = name;
        setType(type);
        this.values = values;
    }

    @Override
    public Object clone() {
        SimpleFilterItem sfi = (SimpleFilterItem) super.clone();
        if (values != null) {
            ArrayList objs = new ArrayList(values.length);

            for (int i = 0; i < values.length; i++) {
                objs.add(values[i]);
            }
            sfi.setValues(objs);
        }
        return sfi;
    }

    public boolean match(Object inputObject) {
        if (inputObject == null) {
            return false;
        }
        Object beanValue = BeanHelper.getProperty(inputObject, name);
        FilterComparator filterComparator = null;
        if (type == FilterType.CUSTOMIZED) {
            // 自定义过滤器
            if (this.customizedFilterComparator == null) {
                throw new SystemException("未定义定制过滤器。");
            }
            filterComparator = customizedFilterComparator;
        } else {
            filterComparator = FilterComparatorFactory.getInstance().getFilterComparator(type);
        }
        if (ignoreCase && (beanValue instanceof String)) {
            // 如果忽略大小写，则将其所有值转换为大写。
            String upperBeanValue = ((String) beanValue).toUpperCase();
            Object[] upperValues = null;
            if (values != null) {
                upperValues = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    Object value = values[i];
                    if (value instanceof String) {
                        value = ((String) value).toUpperCase();
                    }
                    upperValues[i] = value;
                }
            }
            return filterComparator.match(upperBeanValue, upperValues);
        } else {
            return filterComparator.match(beanValue, values);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleFilterItem other = (SimpleFilterItem) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }

        // 自定义类型比较其自定义类是否相等
        if (FilterType.CUSTOMIZED == type) {
            if (this.customizedFilterComparator == null) {
                if (other.customizedFilterComparator != null) {
                    return false;
                }
            } else if (!customizedFilterComparator.equals(other.customizedFilterComparator)) {
                return false;
            }

        }
        return true;
    }

    public boolean isIdentical(FilterItem item) {
        if (!equals(item)) {
            return false;
        }
        SimpleFilterItem other = (SimpleFilterItem) item;
        if (values == null && other.values == null) {
            return true;
        }
        if (values == null || other.values == null) {
            return false;
        }
        if (values.length != other.values.length) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            Object obj1 = values[i];
            Object obj2 = other.values[i];
            if (obj1 == null && obj2 == null) {
                continue;
            }
            if (obj1 == null || !obj1.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public String getName() {
        return name;
    }

    public FilterType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterComparator getCustomizedFilterComparator() {
        return customizedFilterComparator;
    }

    public void setCustomizedFilterComparator(
            FilterComparator customizedFilterComparator) {
        this.customizedFilterComparator = customizedFilterComparator;
    }

    public void setType(FilterType type) {
        if (type != null) {
            operatorName = type.name();
        }
        this.type = type;
    }

    public Object getValue() {
        if (this.values == null || this.values.length == 0) {
            return null;
        }
        return values[0];
    }

    public void setValue(Object value) {
        if (this.values == null || this.values.length == 0) {
            this.values = new Object[1];
        }
        this.values[0] = value;
    }

    public ArrayList getValues() {
        ArrayList parameters = new ArrayList(1);
        if (type == FilterType.IS_EMPTY || type == FilterType.IS_NULL || type == FilterType.IS_NULL) {
            return parameters;
        }
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                parameters.add(values[i]);
            }
        }
        return parameters;
    }

    public void setValues(ArrayList values) {
        if (values != null) {
            this.values = values.toArray();
        } else {
            this.values = null;
        }
    }

    public void setParameters(Object[] values) {
        this.values = values;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(super.toString()).append("[");
        strBld.append("name=").append(name).append(",");
        strBld.append("operatorName=").append(operatorName).append(",");
        strBld.append("values=").append(values).append("]");
        return strBld.toString();
    }
}
