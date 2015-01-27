package com.xt.core.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个类或者方法是异步的，如果方法是异步的，系统将使用一个“deamon”线程处理此请求，
 * 当前线程自动返回。
 * @author albert
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Asynchronized {

}
