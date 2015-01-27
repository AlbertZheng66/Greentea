
package com.xt.core.validation.decl;

import com.xt.core.validation.ValidationException;
import java.lang.annotation.Annotation;
import java.util.regex.Pattern;
import javax.validation.Constraint;
import org.apache.commons.lang.StringUtils;

/**
 * 基于正则表达式的校验基类。
 * @author albert
 */
abstract public class PatternConstraint<T extends Annotation>  implements Constraint<T> {

    /**
     * 需要子类在初始化
     */
    protected Pattern pattern;

    /**
     * 实现类必须返回一个非空的Pattern对象。否则将抛出异常
     * @param anno
     * @return
     */
    abstract Pattern getPattern(T anno);

    @Override
    public void initialize(T anno) {
        pattern = getPattern(anno);
        if (pattern == null) {
            throw new ValidationException("模式不能为空。");
        }
    }


    @Override
    public boolean isValid(Object arg) {
        if (arg == null) {
            return true;
        }
        // 非字符串的情况下忽略此校验（TODO: 是否需要转型呢？）
        if (!(arg instanceof String)) {
            return true;
        }
        String str = (String) arg;
        if (StringUtils.isEmpty(str)) {
            return true;
        }
        return pattern.matcher(str).matches();
    }

}
