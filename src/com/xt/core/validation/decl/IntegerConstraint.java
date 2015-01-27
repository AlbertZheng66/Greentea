package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 整数类型校验。最大值为:9999999999, 默认格式为：^(\\+|\\-)?([1-9]\\d{9})|0$
 * 可通过参数 validation.integer 进行配置。
 * @author albert
 */
public class IntegerConstraint extends PatternConstraint<Integer> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.integer", "^(\\+|\\-)?([1-9]\\d{0,9})|0$");

    @Override
    public Pattern getPattern(Integer integer) {
        String patternStr = integer.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}
