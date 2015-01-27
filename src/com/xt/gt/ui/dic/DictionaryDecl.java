
package com.xt.gt.ui.dic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义字典信息的标注。
 * @author albert
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DictionaryDecl {
    
    String name(); // 字典名称
    
    boolean singleSelection() default true;  // 是否单选
    
    String initialValue() default "";  // 初始值
    
    boolean nullable() default true;     // 是否允许为空
    
    String nullTitle() default "";       // 空值时显示的文本
    
    DictionaryType type() default DictionaryType.comboBox;
}
