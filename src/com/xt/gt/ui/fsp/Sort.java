/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.gt.ui.fsp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义排序的标注，默认为升序类型。
 * @author albert
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sort {
    
    /**
     * 排序类型（默认为升序）。
     * @return
     */
    SortType type() default SortType.ASC;    
}
