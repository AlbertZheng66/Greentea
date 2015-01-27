package com.xt.gt.ui.dic;

import java.io.Serializable;

/**
 * 用于保存定义于域之上的字典信息
 * @author albert
 */
public class DictionaryInfo implements Serializable {
    // 字典名称

    private String name;
    // 是否单选
    private boolean singleSelection = true;
    // 初始值
    private String initialValue;
    // 是否允许为空
    private boolean nullable;
    // 空值时显示的文本
    private String nullTitle;

    public DictionaryInfo() {
    }

    public void load(DictionaryDecl dictionaryDecl) {
        if (dictionaryDecl == null) {
            return;
        }
        name = dictionaryDecl.name();
        singleSelection = dictionaryDecl.singleSelection();
        initialValue = dictionaryDecl.initialValue();
        nullable = dictionaryDecl.nullable();
        nullTitle = dictionaryDecl.nullTitle();
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNullTitle() {
        return nullTitle;
    }

    public void setNullTitle(String nullTitle) {
        this.nullTitle = nullTitle;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }
}
