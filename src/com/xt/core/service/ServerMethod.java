
package com.xt.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识此方法不能在客户端调用的.
 * （对于某些方法只允许在服务器端调用，否则可能存在安全问题）。
 * @FIXME 是否有使用此标注的意义。
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServerMethod {

}
