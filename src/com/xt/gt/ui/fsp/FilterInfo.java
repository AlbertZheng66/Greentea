
package com.xt.gt.ui.fsp;

import com.xt.gt.ui.fsp.filter.FilterType;

/**
 *
 * @author albert
 */
public class FilterInfo {
    private String propertyName;
    
    private String[] values;
    
    private FilterType type = FilterType.EQUALS;
    
    private Filterable filterable;

    public FilterInfo() {
    }
    
    public FilterInfo(String propertyName, String value) {
        this.propertyName = propertyName;
        values = new String[1];
        values[0] = value;
    }
    
    public boolean select(Object value) {
        Filterable _filterable = getFilterable();
        return _filterable.select(value, null);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public Filterable getFilterable() {
        if (filterable == null) {
            filterable = new DefaultFilter();
        }
        return filterable;
    }

    public void setFilterable(Filterable filterable) {
        this.filterable = filterable;
    }
    
}
