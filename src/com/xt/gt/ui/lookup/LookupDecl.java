package com.xt.gt.ui.lookup;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author albert
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LookupDecl {
    
    /**
     * 为了便于定义通用的查找，当查找的名称为空时，此类名称则其作用。
     * @return
     */
    Class<? extends Lookupable> lookupClass();
    
    /**
     * 当前值应对应的Lookup的属性，默认为空。
     * @return
     */
    String propertyName() default "";
    
    /**
     * Lookupable 实现类可自定义的参数。
     * @return
     */
    String params() default "";
    
        
//    /**
//     * 查找的编辑类型
//     * @return
//     */
//    FieldType type() default FieldType.Label;
    
    /**
     * 是否可手工编辑
     * @return
     */
    boolean editable() default false;
    
    /**
     * 如果 Lookup 允许手工输入，此参数定义是否进行校验。
     * @return
     */
    boolean isValidate() default false;
    
//    /**
//     * 是否可被缓存，默认为：ture。
//     * @return
//     */
//    boolean cached() default true;
   
}
