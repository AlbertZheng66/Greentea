
package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 电话号码（不包括手机）校验器, 默认格式为: ^((\d{3,4})|\d{3,4}-)?\d{7,8}$，
 * 可通过参数 validation.phone 进行配置。
 * @author albert
 */
public class PhoneConstraint extends PatternConstraint<Phone> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration
            .getInstance().readString("validation.phone", "^((\\d{3,4})|\\d{3,4}-)?\\d{7,8}$");

    @Override
    public Pattern getPattern(Phone phone) {
        String patternStr = phone.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}

