package com.xt.core.utils;

import com.xt.gt.sys.SystemConfiguration;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:工具类。日期处理工具函数包,包括日期对象、日期字符串相关转换函数等。 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */
public class DateUtils
{
    /*
     * 定义常见的日期格式
     */
    private final static String DEFAULT_DATE_FORMAT = SystemConfiguration.getInstance().readString("defaultDateFormat", "yyyy-MM-dd");

    
    /*
     * 定义常见的日期时间格式
     */
    public final static String DEFAULT_DATE_TIME_FORMAT = SystemConfiguration.getInstance().readString("defaultDatetimeFormat", "yyyy-MM-dd HH:mm:ss");

    /**
     * 将日期格式从 java.util.Calendar 转换为 java.sql.Timestamp 格式
     * @param date java.util.Calendar 格式表示的日期
     * @return     java.sql.Timestamp 格式表示的日期
     */
    public static java.sql.Timestamp convertCalendarToTimestamp (java.util.Calendar date)
    {
        if (date == null)
        {
            return null;
        }
        return new java.sql.Timestamp(date.getTimeInMillis());
    }
    
    /**
     * 将日期格式从 java.util.Date 转换为 java.sql.Date 格式
     * @param date java.util.Calendar 格式表示的日期
     * @return     java.sql.Timestamp 格式表示的日期
     */
    public static java.sql.Date convertUtilDateToSqlDate (java.util.Date date)
    {
        if (date == null)
        {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
    
    /**
     * 将日期格式从 java.util.Date 转换为 java.sql.Date 格式
     * @param date java.util.Calendar 格式表示的日期
     * @return     java.sql.Timestamp 格式表示的日期
     */
    public static java.util.Date convertSqlDateToUtilDate (java.sql.Date date)
    {
        if (date == null)
        {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }


    /**
     * 将日期格式从 java.util.Timestamp 转换为 java.util.Calendar 格式
     * @param ts    java.sql.Timestamp 格式表示的日期
     * @return     java.util.Calendar 格式表示的日期
     */
    public static java.util.Calendar convertTimestampToCalendar (java.sql.Timestamp ts)
    {
        if (ts == null)
        {
            return null;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts.getTime());
        return gc;
    }

    /**
     * 解析一个字符串，形成一个Calendar对象，适应各种不同的日期表示法
     * @param dateStr 期望解析的字符串，注意，不能传null进去，否则出错
     * @return 返回解析后的Calendar对象
     * <br>
     */
    public static Calendar parseCalendar (String dateStr)
    {
        if (dateStr == null || dateStr.trim().length() == 0)
        {
            return null;
        }
        Date result = null;
		try {
			result = parseDate(dateStr, DEFAULT_DATE_FORMAT);
		} catch (ParseException e) {
			return null;
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        return cal;
    }
    
    /**
     * 解析一个字符串，形成一个Calendar对象，适应各种不同的日期表示法
     * @param dateStr 期望解析的字符串，注意，不能传null进去，否则出错
     * @return 返回解析后的Calendar对象
     * <br>
     */
    public static Calendar parseCalendar (String dateStr, String dateFormat)
    {
        if (dateStr == null || dateStr.trim().length() == 0)
        {
            return null;
        }
        Date result = null;
		try {
			result = parseDate(dateStr, dateFormat);
		} catch (ParseException e) {
			return null;
		}
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        return cal;
    }


    /**
     * 将一个日期转成日期时间格式，格式这样  2002-08-05 21:25:21
     * @param date  期望格式化的日期对象
     * @return 返回格式化后的字符串
     * <br>
     * <br>例：
     * <br>调用：
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateTimeStr(date);
     * <br>返回：
     * <br> ret = "2002-12-04 09:13:16";
     */
    public static String toDateTimeStr (Calendar date)
    {
        if (date == null)
        {
            return null;
        }
        return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(date.getTime());
    }

    /**
     * 将一个日期转成日期格式，格式这样  2002-08-05
     * @param date  期望格式化的日期对象
     * @return 返回格式化后的字符串
     * <br>
     * <br>例：
     * <br>调用：
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateStr(calendar);
     * <br>返回：
     * <br>ret = "2002-12-04";
     */
    public static String toDateStr (Calendar date)
    {
        return toDateStr(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 将一个日期转成日期格式
     * @param date 期望格式化的日期对象
     * @param dateFormat 日期的格式
     * @return 返回格式化后的字符串
     */
    public static String toDateStr (Calendar date, String dateFormat)
    {
        if (date == null)
        {
            return null;
        }
        return new SimpleDateFormat(dateFormat).format(date.getTime());
    }


   
    /**
     * 日期相减运算，返回两个日期间隔天数
     * 如果输入参数为null；则返回永远为0
     * @param d1 终止日期（被减数）
     * @param d2 起始日期（减数）
     * @return  两个日期间隔天数，不足天计为0
     */
    public static int calendarMinus (Calendar d1, Calendar d2)
    {
        if (d1 == null || d2 == null)
        {
            return 0;
        }

        long t1 = d1.getTimeInMillis();
        long t2 = d2.getTimeInMillis();
        long daylong = 3600 * 24 * 1000;
        t1 = t1 - t1 % (daylong);
        t2 = t2 - t2 % (daylong);

        long t = t1 - t2;
        return (int) (t / (daylong));
    }

    /*
     * 内部方法，根据某个索引中的日期格式解析日期
     * @param dateStr 期望解析的字符串
     * @param format 日期格式
     * @return 返回解析结果
     * @throws ParseException
     */
    public static Date parseDate (String dateStr, String format)
     throws ParseException
    {
        DateFormat df = null;
        df = new SimpleDateFormat(format);
        return df.parse(dateStr);
    }

}
