

package com.xt.core.validation.decl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.ConstraintValidator;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * HTTP 地址校验器, 默认格式为: ^https?://([\\w\\-]+.)+[\\w\\-]+(/[\\w\\-\\./?%&=]*)?$，
 * 可通过参数 validation.http 进行配置。
 * @author albert
 */
@ConstraintValidator(DoubleConstraint.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@java.lang.annotation.Inherited
public @interface Double {

    /**
     * 错误消息
     * @return
     */
    String message() default "双精度数的格式错误。";

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

    /**
     * 指定列的小数点右边的位数, 默认为 2.
     * @return
     */
    int scale() default 2;
}

