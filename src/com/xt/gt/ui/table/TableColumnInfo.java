
package com.xt.gt.ui.table;

import com.xt.gt.ui.dic.DictionaryInfo;
import java.io.Serializable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 封装了所有的表列信息。
 * @author albert
 */
public class TableColumnInfo implements Comparable, Serializable {
    
    /**
     * 列对应的属性名称。
     */
    private String propertyName;

    /**
     * 列对应的属性类型名称。
     */
    private String propertyClassName;

    /**
     * 列对应的属性类型名称。
     */
    private Class propertyClass;
    
 /**
     * 列的标题信息
     * @return
     */
   private String title;
   
   /**
     * 表列的排列顺序，从 0 开始依次排序，如果两个顺序相等，
     * 则无法此两列的排序顺序存在随机性。-1 表示随意排序。
     * @return
     */
   private int seq = Integer.MAX_VALUE;
    
    /**
     * 列的缺省宽度(默认为50)
     * @return
     */
    private int  width = 80;
    
    /**
     * 是否可见
     * @return
     */
    private boolean visiable = true;
    
    /**
     * 是否只读
     * @return
     */
    private boolean readonly = false;
    
    /**
     * 此列是否暂时失效
     * @return
     */
     private boolean disable = false;
    
    /**
     * 表头的对齐方式
     * @return
     */
     private int alignment = SwingConstants.CENTER;
     
     
    private TableCellRenderer cellRenderer;

    private TableCellEditor cellEditor;

    private boolean resizable = true;

    // 定义于此段上的字典信息
    private DictionaryInfo dictionaryInfo;
     

    public TableColumnInfo() {
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int compareTo(Object o) {
        if (seq < 0 || o == null || !(o instanceof TableColumnInfo)) {
            return Integer.MAX_VALUE;
        }
        return seq - ((TableColumnInfo)o).seq;
    }

    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    public void setCellEditor(TableCellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }

    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public void setCellRenderer(TableCellRenderer cellRenderer) {
        this.cellRenderer = cellRenderer;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public String getPropertyClassName() {
        return propertyClassName;
    }

    public void setPropertyClassName(String propertyClassName) {
        this.propertyClassName = propertyClassName;
    }

    public Class getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(Class propertyClass) {
        this.propertyClass = propertyClass;
    }

    

    public DictionaryInfo getDictionaryInfo() {
        return dictionaryInfo;
    }

    public void setDictionaryInfo(DictionaryInfo dictionaryInfo) {
        this.dictionaryInfo = dictionaryInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableColumnInfo other = (TableColumnInfo) obj;
        if ((this.propertyName == null) ? (other.propertyName != null) : !this.propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
     
}
