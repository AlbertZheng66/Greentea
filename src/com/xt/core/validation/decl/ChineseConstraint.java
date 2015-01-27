package com.xt.core.validation.decl;

import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 中文校验器, 默认格式为: ^[\u4e00-\u9fa5]*$，
 * 可通过参数 validation.chinese 进行配置。
 * @author albert
 */
public class ChineseConstraint extends PatternConstraint<Chinese> {

    /**
     * 默认的校验模式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.chinese", "^[\\u4e00-\\u9fa5]*$");

    @Override
    public Pattern getPattern(Chinese chinese) {
        String patternStr = chinese.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = DEFAULT_PATTERN;
        }
        return Pattern.compile(patternStr);
    }
}

