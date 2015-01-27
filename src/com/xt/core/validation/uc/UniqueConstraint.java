
package com.xt.core.validation.uc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应该将 此标注定义于属性之上，然后再通过相应的方法关联到对应的 TextField。
 * @author albert
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface UniqueConstraint {
    /**
     * 唯一约束的名称用于多字段唯一约束的情况。单值唯一约束的名称即是其属性的名称。
     * @return
     */
    String name() default "";
    
    /**
     * 用于此唯一约束的校验类，默认为 DefaultUniqueValidateService。
     * @return
     */
    Class<? extends UniqueValidateService> validateClass() default DefaultUniqueValidateService.class;
    
    /**
     * 是否进行自动校验，即输入之后立即进行校验。（本地进行进行实时校验）
     * @return
     */
    boolean auto() default false;
    
}
