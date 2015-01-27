
package com.xt.core.validation.decl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.ConstraintValidator;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于校验电子邮件的校验标注。
 * @author albert
 */
@ConstraintValidator(PhoneConstraint.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@java.lang.annotation.Inherited
public @interface Phone {

    /**
     * 错误消息
     * @return
     */
    String message() default "电话号码的格式不正确。";

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

