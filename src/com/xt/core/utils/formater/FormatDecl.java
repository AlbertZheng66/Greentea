
package com.xt.core.utils.formater;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 格式化标注，用于在 Bean 中定义属性的格式化样式。
 * @author albert
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormatDecl {

//    String name();

    String value();

    String param() default "";
}
