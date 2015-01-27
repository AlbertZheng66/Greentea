
package com.xt.gt.ui.table;

import com.xt.gt.ui.table.editor.NullTableCellEditor;
import com.xt.gt.ui.table.renderer.NullTableCellRenderer;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author albert
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnInfo {
    /**
     * 列的标题信息
     * @return
     */
    String title();
    
    /**
     * 表列的排列顺序，从 0 开始依次排序，如果两个顺序相等，
     * 则无法此两列的排序顺序存在随机性。默认排序将其排在最后。
     * @return
     */
    int seq() default Integer.MAX_VALUE;
    
    /**
     * 列的缺省宽度
     * @return
     */
    int  width() default 20;
    
    /**
     * 是否可见
     * @return
     */
    boolean visible() default true;
    
    /**
     * 是否只读
     * @return
     */
    boolean readonly() default false;
    
    /**
     * 此列是否暂时失效
     * @return
     */
    boolean disable() default false;
    
    /**
     * 表头的对齐方式
     * @return
     */
    int alignment() default SwingConstants.CENTER;

    /**
     * 用户自定义的编辑器。
     * @return
     */
    Class<? extends TableCellEditor> cellEditor() default NullTableCellEditor.class;

    /**
     * 用户自定义的呈现器。
     * @return
     */
    Class<? extends TableCellRenderer> cellRenderer() default NullTableCellRenderer.class;

    /**
     * 用户是否可以重新设置列宽度，默认为 true。
     * @return
     */
    boolean resizable() default true;

    /**
     * 表示此属性是一个复杂的类，需要读取类定义的字段信息
     * @return
     */
    boolean nested() default false;
    
}
