
package com.xt.core.validation.decl;


import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author albert
 */
public class HTTPConstraint  extends PatternConstraint<HTTP> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.http", "^https?://([\\w-]+.)+[\\w-]+(/[\\w\\-\\./?%&=]*)?$");

    @Override
    public Pattern getPattern(HTTP http) {
        String patternStr = http.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}

