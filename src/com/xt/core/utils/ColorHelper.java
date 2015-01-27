package com.xt.core.utils;

import java.awt.Color;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class ColorHelper
{
    public ColorHelper ()
    {
    }

    /**
     * 将HTML格式的颜色表示形式转换成Color对象.
     * @param str String 十六进制的颜色表示方法,可以以"#"开头,如"#12CC22",或者"12CC22"
     * @return Color
     */
    public static Color toColor (String color)
    {
        if (RegExpUtils.isColorDigitals(color))
        {
            //如果以#开头,去掉#
            if (color.startsWith("#"))
            {
                color = color.substring(1);
            }

            return new Color(Integer.parseInt(color.substring(0, 2), 16),
                             Integer.parseInt(color.substring(2, 4), 16),
                             Integer.parseInt(color.substring(4, 6), 16));
        }
        return null;
    }

}
