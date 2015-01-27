package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 移动电话号码校验器, 默认格式为: ^1\\d{12,12}$，
 * 可通过参数 validation.mobile 进行配置。
 * @author albert
 */
public class MobileConstraint extends PatternConstraint<Mobile> {

    /**
     * 默认的移动电话校验格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.mobile", "^1\\d{12,12}$");

    @Override
    public Pattern getPattern(Mobile mobile) {
        String patternStr = mobile.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}

