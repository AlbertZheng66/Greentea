package com.xt.core.utils;

import com.xt.core.exception.BadParameterException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import com.xt.core.exception.POException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class RandomUtils
{
    /**
     * 用于产生下一个随机数的种子
     */
    private static volatile long seed = System.currentTimeMillis();

    private static final Random random = new Random(seed);

    public RandomUtils()
    {
    }
    
    /**
     * 从列表中随机选择一行记录返回。
     * @param <T>
     * @param list
     * @return 
     */
    static public <T> T select(List<T> list) {
        List<T> selected = select(list, 1);
        return (selected == null || selected.isEmpty()) ? null : selected.get(0);
    }
    
    /**
     * 从列表中随机选择多条记录返回。
     * @param <T>
     * @param list
     * @return 
     */
    static public <T> List<T> select(List<T> list, int n) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (n <= 0 || n > list.size()) {
            throw new BadParameterException(String.format("%d is less than zero or greater than the length[%d] of the list.",
                    n, list.size()));
        }
        List<T> operatedList = new ArrayList<T>(list.size());
        operatedList.addAll(list);
        // 选择全部
        if (n == list.size()) {
            return operatedList;
        }
        List selected = new ArrayList<T>(n);
        for (int i = 0; i < n; i++) {
            int index = random.nextInt(operatedList.size());
            Object obj = operatedList.get(index);
            selected.add(obj);
            operatedList.remove(index);
        }
        
        return selected;
    }

    /**
     *
     * @param tableName String 如果表名称为空,不作检测.
     * @param colName String
     * @param count int
     * @param conn Connection
     * @return String
     */
    public static String generateKeyCode(String tableName, String colName, int count,
                                         Connection conn)
    {
        return generateRandomKeyCode(count);
    }

    public static String generateRandomKeyCode(int count)
    {
        //目前产生13位编码
        long time = System.currentTimeMillis();
        Random random = new Random();

        //上一次的种子
        random.setSeed(seed + time);
        long ran = random.nextLong();

        try
        {
            Thread.currentThread().sleep(1);
        }
        catch (InterruptedException ex)
        {
            /**
             * 不作任何处理
             */
        }

        //此次的随机数作为下一次的种子
        seed = ran;
        String keyCode = Long.toString(time) + Long.toString(ran).substring(0, 8);
        //形成18位随机数
        return keyCode;
    }

    /**
     * 产生20位的随机代码，前13位为系统时间，后面七位为随机数
     * @return String
     */
    public static String generateRandomKeyCode()
    {
        //目前产生13位编码
        long time = System.currentTimeMillis();
        Random random = new Random();

        //上一次的种子
        random.setSeed(seed + time);
        long ran = random.nextLong();

        //此次的随机数作为下一次的种子
        seed = ran;
        String keyCode = Long.toString(time) + Long.toString(Math.abs(ran)).substring(0, 7);
        //形成20位随机数
        return keyCode;
    }

    /**
     * 产生顺序数.产生指定表的指定列的最多数,依次产生
     * @param tableName String 表名称不能为空
     * @param colName String 列名称不能为空
     * @param conn Connection
     * @throws POException
     * @return String
     */
    public static String generateSequenceKeyCode(String tableName, String colName, Connection conn)
            throws POException
    {
        return Long.toString(generateLongSequence(tableName, colName, conn));
    }

    public static long generateLongSequence(String tableName, String colName, Connection conn)
            throws POException
    {
        long keyCode = 0;
        String sql = "SELECT MAX(" + colName + ") FROM " + tableName;
        try
        {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next())
            {
                keyCode = rs.getLong(1) + 1;
            }
        }
        catch (SQLException ex)
        {
            throw new POException("", ex);
        }
        return keyCode;
    }

    /**
     * 产生顺序数.产生指定表的指定列的最多数,依次产生,并在不足指定位数的编码前面补零
     * @param tableName String 表名称不能为空
     * @param colName String 列名称不能为空
     * @param codeLength String 要求返回编码的长度，不能为空
     * @param conn Connection
     * @throws POException
     * @return String
     */
    public static String generateSequenceKeyCode(String tableName, String colName, int codeLength,
                                                 Connection conn)
            throws POException
    {
        String keyCode = generateSequenceKeyCode(tableName, colName, conn);
        if (keyCode.length() > codeLength)
        {
            throw new POException("KeyCodeGenerator: length of " + colName + " in " + tableName
                                  + " is bigger than codeLength.");
        }
        int add0 = codeLength - keyCode.length();
        for (int i = 0; i < add0; i++)
        {
            keyCode = "0" + keyCode;
        }
        return keyCode;
    }

    public static String random(int count)
    {
        return random(count, false, false);
    }

    public static String randomAscii(int count)
    {
        return random(count, 32, 127, false, false);
    }

    public static String randomAlphabetic(int count)
    {
        return random(count, true, false);
    }

    public static String randomAlphanumeric(int count)
    {
        return random(count, true, true);
    }

    public static String randomNumeric(int count)
    {
        return random(count, false, true);
    }

    private static String random(int count, boolean letters, boolean numbers)
    {
        return random(count, 0, 0, letters, numbers);
    }

    private static String random(int count, int start, int end, boolean letters, boolean numbers)
    {
        return random(count, start, end, letters, numbers, null);
    }

    public static String random(int count, String set)
    {
        return random(count, set.toCharArray());
    }

    public static String random(int count, char[] set)
    {
        return random(count, 0, set.length - 1, false, false, set);
    }

    /**
     * 产生一个随机字符串，其长度不大于指定长度
     * @param maxCount    构造字符串的最大长度（字符个数）
     * @param start    构造字符串所需素材的最小值(字符编码值)
     * @param end      构造字符串所需素材的最大值(字符编码值)
     * @param letters  true 返回字符串中是否可以包含字母字符
     * @param numbers  true 返回字符串中是否可以包含数字字符
     * @param set      构造字符串所需素材字符集合
     * @return         返回构造的随机字符串
     * @require count > 0 and start > 0 and end > 0
     * @require 当素材字符集合set非空时，参变量start、end取值应为set的有效下标
     */
    private static String random(int maxCount, int start, int end, boolean letters, boolean numbers,
                                 char[] set)
    {
        //产生一个随机数来代表此次产生的字符数
        int count = random.nextInt(maxCount);
        return randomFixLength(count, start, end, letters, numbers, set);
    }

    public static String randomFixLength(int count)
    {
        return randomFixLength(count, false, false);
    }
    
    public static String randomRange(int start, int end)
    {
        int interval = Math.abs(end - start);
        random.setSeed(System.nanoTime());
        return String.valueOf(start + random.nextInt(interval));
    }

    public static String randomFixLengthAscii(int count)
    {
        return randomFixLength(count, 32, 127, false, false);
    }

    public static String randomFixLengthAlphabetic(int count)
    {
        return randomFixLength(count, true, false);
    }

    public static String randomFixLengthAlphanumeric(int count)
    {
        return randomFixLength(count, true, true);
    }

    /**
     * 够造指定长度的随机数字字符串
     * @param count  构造字符串的长度（字符个数）
     * @return       返回构造的随机字符串
     * <br>
     * <br>例：
     * <br>调用：String s = RandomUtils.randomFixLengthNumeric(20);
     * <br>返回值：s = "23547933728487290410";
     * <br>返回值由数字组成
     */
    public static String randomFixLengthNumeric(int count)
    {
        return randomFixLength(count, false, true);
    }

    private static String randomFixLength(int count, boolean letters, boolean numbers)
    {
        return randomFixLength(count, 0, 0, letters, numbers);
    }

    private static String randomFixLength(int count, int start, int end, boolean letters,
                                          boolean numbers)
    {
        return randomFixLength(count, start, end, letters, numbers, null);
    }

    public static String randomFixLength(int count, String set)
    {
        return randomFixLength(count, set.toCharArray());
    }

    public static String randomFixLength(int count, char[] set)
    {
        return randomFixLength(count, 0, set.length - 1, false, false, set);
    }

    private static String randomFixLength(int count, int start, int end, boolean letters,
                                          boolean numbers,
                                          char[] set)
    {
        StringBuffer buffer = new StringBuffer();
        if (count <= 0)
        {
            return "";
        }
        if (start < 0 || end < 0 || start > end)
        {
            return null;
        }
        if (set != null && (start > set.length || end - start + 1 > set.length))
        {
            return null;
        }
        if (set != null && end >= set.length)
        {
            return null;
        }

        //随机数据的取值范围
        int gap = 0; //随机数的取值范围
        if ((start == 0) && (end == 0))
        {
            //默认为从' '(空格)到字符'z'的ascii值
            end = (int) 'z';
            start = (int) ' ';
            gap = end - start + 1; //随机数的取值范围
            if (!letters && !numbers)
            {
                //如果类型无限制，则取值范围为正整数类型Integer的可表示范围
                start = 0;
                end = Integer.MAX_VALUE;
                gap = end - start; //随机数的取值范围
            }
        }
        else
        {
            gap = end - start + 1; //随机数的取值范围
        }

        while (count-- != 0)
        {
            char ch;
            if (set == null)
            {
                ch = (char) (random.nextInt(gap) + start);
            }
            else
            {
                ch = set[random.nextInt(gap) + start];
            }

            if ((letters && numbers && Character.isLetterOrDigit(ch)) //定义为字符和数字，生成为字符或数字
                || (letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) //定义为数字，生成为数字
                || (!letters && !numbers)) //定义不限定字符或数字
            {
                buffer.append(ch);
            }
            else
            {
                count++; //
            }
        }
        return buffer.toString();
    }
    
    /**
     * 这个函数(Marsaglia 2003）是随机函数中性价比最好的之一。可以基于hashCode 和 nanoTime 计算随机数，所得的结果即是不可以预知的，
     * 而且几乎每次运行都不同。
     * int seed = ((this.hashCode()) ^ (int)System.nanoTime());
     * @param y
     * @return 
     */
    static public int xorshift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

}
