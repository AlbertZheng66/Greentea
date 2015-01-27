
package com.xt.gt.ui.fsp;

import com.xt.gt.ui.fsp.filter.FilterComparator;
import com.xt.gt.ui.fsp.filter.FilterType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author albert
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterDecl {
    
    /**
     * 名称，默认为属性名称。
     * @return
     */
    String name() default "";
    
    /**
     * 过滤的类型，默认为“以过滤值开头”。
     * @return
     */
    FilterType type() default FilterType.STARTS_WITH;

    /**
     * 如果类型过滤类型定义为自定义的情况下，需要使用自定义过滤器。
     * @return
     */
    Class<? extends FilterComparator> filterComparator() default NullFilterComparator.class;
    
    /**
     * 是否是“即时过滤”。所谓即时是指用户在输入查询条件之后马上进行查询。
     * @return
     */
    boolean immediate() default false;
    
    /**
     * 是否忽略大小写
     * @return
     */
    boolean ignoreCase() default false;
    
    /**
     * 是否自动删除其前后空格
     * @return
     */
    boolean autoTrim() default false;
}
