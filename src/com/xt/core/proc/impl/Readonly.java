
package com.xt.core.proc.impl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一个标识标注， 用于标注一个方法或者一个类是否是只读的,
 * 如果是,可以将其分配给备份数据库的情况,
 * 以减少主数据库的压力.
 * 如果此标注定义在类型之上，表示此类的所有方法都是只读的；
 * 如果定义在方法之上，表示此方法为只读的。
 * @author albert
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Readonly {

}
