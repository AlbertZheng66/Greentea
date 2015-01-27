package com.xt.gt.ui.lookup;

/**
 * 用于定义查找框的输入样式
 * @author albert
 */
public enum FieldType {
    Label, TextField, ComboBox;
    
    public String toDisplayString() {
        return toString();
    }
            
    @Override
    public String toString() {
        if (this == Label) {
            return "标签";
        } else if (this == TextField) {
            return "文本框";
        } else {
            return "下拉列表框";
        }
    }
}
