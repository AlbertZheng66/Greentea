package com.xt.core.utils.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:检测注释的正则表达式。 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class CommentRegExp
{
    public CommentRegExp()
    {
    }

    /**
     * 检验字符串是否含有单行注释
     * @param str 被检验的字符串
     * @return 如果含有单行注释，则返回真，否则返回假。
     */
    public boolean isSingleLineComment (String str) {
        String slcre = ".*//.*\n";  //单行注释的正则表达式
        Pattern p = Pattern.compile(slcre);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 检验字符串是否含有多行注释
     * @param str 被检验的字符串
     * @return 如果含有单行注释，则返回真，否则返回假。
     */
    public boolean isMultiLinesComment (String str) {
        String slcre = ".*/\\*.*\n*.*\\*/";  //多行注释的正则表达式
        Pattern p = Pattern.compile(slcre);
        Matcher m = p.matcher(str);
        return m.matches();
    }


}
