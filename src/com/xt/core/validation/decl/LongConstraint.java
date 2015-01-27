package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 整数类型校验。最大值为:99999999999999999999, 默认格式为：^((\\+|\\-)?([1-9]\\d{9})|0)$
 * 可通过参数 validation.integer 进行配置。
 * @author albert
 */
public class LongConstraint extends PatternConstraint<Long> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.long", "^((\\+|\\-)?([1-9]\\d{0,17})|0)$");

    @Override
    public Pattern getPattern(Long _long) {
        String patternStr = _long.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}
