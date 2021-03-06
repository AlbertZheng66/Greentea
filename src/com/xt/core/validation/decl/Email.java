
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
@ConstraintValidator(EmailConstraint.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
// @java.lang.annotation.Inherited
public @interface Email {

    /**
     * 错误消息
     * @return
     */
    String message() default "电子邮件格式错误。";

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
