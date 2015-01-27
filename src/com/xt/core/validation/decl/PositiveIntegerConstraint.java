package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 正整数类型校验。最大值为:99999999999。默认格式为: ^([1-9]\\d{9})|0$，
 * 可通过参数 validation.positiveInteger 进行配置。
 * @author albert
 */
public class PositiveIntegerConstraint extends PatternConstraint<PositiveInteger> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.integer", "^([1-9]\\d{9})|0$");;

    @Override
    public Pattern getPattern(PositiveInteger pi) {
        String patternStr = pi.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}
