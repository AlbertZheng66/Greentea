
package com.xt.core.validation.uc;

import com.xt.core.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 唯一约束的封装类，包括了和唯一属性绑定的类，唯一约束相关的属性名称以及服务器端校验类。
 * @author albert
 */
public class UniqueConstraintInfo {
    /**
     * 绑定的类
     */
    private final Class bindingClass;
    
    /**
     * 校验相关的属性
     */
    private final List<String> propertyNames = new ArrayList(2);
    
    /**
     * 当前属性和值的映射关系。
     */
    private Map<String, Object> values = new HashMap();
    
    /**
     * 服务器端校验类
     */
    private Class<? extends UniqueValidateService> validateClass;

    public UniqueConstraintInfo(Class bindingClass, String[] propertyNames) {
        this.bindingClass = bindingClass;
        if (propertyNames != null) {
            for (String propertyName : propertyNames) {
                this.propertyNames.add(propertyName);
            }
        }
    }
    
    public UniqueConstraintInfo(Class bindingClass) {
        this.bindingClass = bindingClass;
    }
    
    public boolean isUnique(Object[] values) {
        if (this.values.isEmpty() || values == null) {
            return true;
        }
        if (this.propertyNames.size() != values.length) {
            throw new ValidationException(String.format("唯一性约束的传入参数值[%d]与属性值[%d]的个数不等。", values.length, propertyNames.size()));
        }
        List ucList = new ArrayList(this.values.size());
        for(Map.Entry<String, Object> entry : this.values.entrySet()) {
            ucList.add(entry.getValue());
        }
        
        Object[] ucObjects = ucList.toArray();
        for (int i = 0; i < ucObjects.length; i++) {
            if (!equals(values[i], ucObjects[i])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 唯一性约束的比较方法，子类可通过重载此方法改变校验规则。默认规则是，如果两个对象
     * 皆为空或者地址相等，则返回 true；否则使用对象的equals方法进行校验其相等性。 
     * @param o1 比较对象
     * @param o2 比较对象
     * @return 对象是否想等。
     */
    protected boolean equals (Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == o2) {
            return true;
        }
        if (o1 != null && o1.equals(o2)) {
            return true;
        }
        return false;
    }

    public Class getBindingClass() {
        return bindingClass;
    }

    public String[] getPropertyNames() {
        return propertyNames.toArray(new String[propertyNames.size()]);
    }

    public Class<? extends UniqueValidateService> getValidateClass() {
        return validateClass;
    }

    public void setValidateClass(Class<? extends UniqueValidateService> validateClass) {
        this.validateClass = validateClass;
    }

    public Object getValue(String propertyName) {
        return values.get(propertyName);
    }

    public void setValue(String propertyName, Object value) {
        if (!propertyNames.contains(propertyName)) {
            propertyNames.add(propertyName);
        }
        this.values.put(propertyName, value);
    }
    
    public Object[] getValues() {
        List _values = new ArrayList(propertyNames.size());
        for(Map.Entry entry : this.values.entrySet()) {
            _values.add(entry.getValue());
        }
        return _values.toArray(new Object[_values.size()]);
    }

}
