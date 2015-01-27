package com.xt.core.utils.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 正则表达式校验方法。通过正则表达式对“数值”类型，“浮点”类型，Email地址<br>
 *                 类型进行校验。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class REValidator
{
    /**
     * “数值”类型
     */
    public static final String INTEGER = "[\\+|-]?\\d";

    /**
     * “浮点”类型
     */
    public static final String FLOAT = "[\\+|-]?\\d+\\.?\\d*";

    /**
     * “Email地址”类型
     */
    public static final String EMAIL = "^[a-zA-Z_][\\w\\.]*@[\\w\\-]{2,}\\.[\\w\\-]{2,}[\\w\\.\\-]*";

    public REValidator()
    {
    }

    /**
     * 测试输入字符串是否是整型
     * @param number 被测试的字符串
     * @return 被测试的字符串是否数值类型，是则返回真，否则返回假。
     */
    public static boolean isNumber (String number) {
        Pattern p = Pattern.compile(INTEGER + "+");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 测试输入字符串是否是整型，并且最小长度大于等于指定的长度
     * @param number 被测试的字符串
     * @param minLength 最小长度
     * @return 被测试的字符串是否数值类型且最小长度大于等于指定的长度，是则返回真，否则返回假。
     */
    public static boolean isNumber (String number, int minLength) {
        Pattern p = Pattern.compile(INTEGER + "{" + minLength + ",}");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 测试输入字符串是否是整型，并且最小长度大于等于指定的长度，最大长度小于等于指定的最大长度。
     * @param number 被测试的字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 被测试的字符串是否数值类型且最小长度大于等于指定的长度，是则返回真，否则返回假。
     */
    public static boolean isNumber (String number, int minLength, int maxLength) {
        Pattern p = Pattern.compile(INTEGER + "{" + minLength + "," + maxLength + "}");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * 测试输入的字符串是否为浮点型
     * @param floatStr 被测试的字符串
     * @return 被测试的字符串是否为浮点型，是则返回真，否则返回假。
     */
    public static boolean isFloat (String floatStr) {
        Pattern p = Pattern.compile(FLOAT);
        Matcher m = p.matcher(floatStr);
        return m.matches();
    }

    /**
     * 测试输入的字符串是否是合法的Email地址
     * @param email 被测试的字符串
     * @return 被测试的字符串是否为合法的Email地址，是则返回真，否则返回假。
     */
    public static boolean isEmail (String email) {
        Pattern p = Pattern.compile(EMAIL);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
