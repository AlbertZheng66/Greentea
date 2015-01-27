package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author albert
 */
public class EmailConstraint extends PatternConstraint<Email> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.email", "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*.\\w+([-.]\\w+)*$");

    @Override
    public Pattern getPattern(Email email) {
        String patternStr = email.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr =  DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}

