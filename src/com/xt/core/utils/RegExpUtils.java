package com.xt.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class RegExpUtils
{
    public RegExpUtils()
    {
    }

    /**
     * 产生匹配参数
     * @param patternStr String
     * @param input String
     * @param flags int
     * @return Matcher
     */
    public static Matcher generate (String patternStr, String input, int flags) {
        Pattern checkFrom = Pattern.compile(patternStr, flags);
        return checkFrom.matcher(input);
    }


    public static Matcher generate (String patternStr, String input) {
        Pattern checkFrom = Pattern.compile(patternStr);
        return checkFrom.matcher(input);
    }
    
    /**
     * 检验字符串是否表示颜色的6位16进制数字字符串
     * @param str String
     * @return boolean
     */
    public static boolean isInteger (String str) {
    	if (str == null || str.trim().length() == 0) {
    		return false;
    	}
        Matcher intMth = RegExpUtils.generate("\\d{1,11}", str);
        return intMth.matches();
    }

    /**
     * 检验字符串是否表示颜色的6位16进制数字字符串
     * @param str String
     * @return boolean
     */
    public static boolean isColorDigitals (String str) {
        Matcher colorDigitals = RegExpUtils.generate("#?[\\dabcdef]{6}", str,
                                                     Pattern.CASE_INSENSITIVE);
        return colorDigitals.matches();
    }


}
