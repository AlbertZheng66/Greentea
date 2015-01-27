package com.xt.core.validation.decl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.ConstraintValidator;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 中文校验器, 默认格式为: ^[\u4e00-\u9fa5]*$，
 * 可通过参数 validation.chinese 进行配置。
 * @author albert
 */
@ConstraintValidator(ChineseConstraint.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@java.lang.annotation.Inherited
public @interface Chinese {

    /**
     * 错误消息
     * @return
     */
    String message() default "只允许输入中文字符。";

    /**
     * 组信息
     * @return
     */
    String[] groups() default {};

    /**
     * 自定义的模式
     * @return
     */
    String pattern() default "";
}


