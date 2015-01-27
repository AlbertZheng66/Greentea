
package com.xt.core.validation.decl;


import com.xt.core.utils.VarTemplate;
import com.xt.gt.sys.SystemConfiguration;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author albert
 */
public class DoubleConstraint  extends PatternConstraint<Double> {

    /**
     * 默认的邮件格式
     */
    private final static String DEFAULT_PATTERN = SystemConfiguration.getInstance()
            .readString("validation.double", "^(\\+|\\-)?(([1-9]\\d{0,17})|0)(\\.\\d\\d{0,${scale}})?$");

    @Override
    public Pattern getPattern(Double _double) {
        String patternStr = _double.pattern();
        if (StringUtils.isEmpty(patternStr)) {
            patternStr = VarTemplate.format(DEFAULT_PATTERN, _double.scale() - 1);
        }
        return Pattern.compile(patternStr);
    }
}
