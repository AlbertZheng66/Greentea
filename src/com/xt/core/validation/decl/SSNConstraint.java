package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 身份证校验器, 默认格式为: ^\\d{15}|\\d{18}$，
 * 可通过参数 validation.ssn 进行配置。
 * @author albert
 */
public class SSNConstraint extends PatternConstraint<SSN> {

    /**
     * 默认的邮件格式
     */
    private final static String defaultPattern = "^\\d{15}|\\d{18}$";

    @Override
    public Pattern getPattern(SSN ssn) {
        String patternStr = ssn.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = SystemConfiguration.getInstance().readString("validation.ssn", defaultPattern);
        }
        return Pattern.compile(patternStr);
    }
}
