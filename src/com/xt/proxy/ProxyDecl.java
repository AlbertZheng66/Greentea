package com.xt.proxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个标注用于给某个方法指定代理，而不是采用框架统一提供的代理方式。
 * @author albert
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProxyDecl {
	
	String name(); // 代理的名称
	
	/**
	 * 代理的参数，具体含义由代理自行解释。
	 * @return
	 */
	String params() default "";

}
