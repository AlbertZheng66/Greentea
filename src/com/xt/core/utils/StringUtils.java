package com.xt.core.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.commons.codec.binary.Base64;

/**
 * <p>Title: 基础框架－工具类</p>
 * <p>Description:工具类，提供各种处理字符串的方法。 包括：
 * 1. 将空（null）转换成长度为0的字符串;
 * 2. 由骆驼记法转换为下划线分割记法;
 * 3. 由下划线分割记法转换为骆驼记法; 
 * 4. 将源字符串数组拷贝到目的字符串数组中;
 * 5. 将字符串数组拼装成以指定分隔符分割的字符串形式;
 * 6. 判断一个字符串是否为真 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public final class StringUtils
{
    /**
     * 缺省的字符集
     */
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    /**
     * 一个空格
     */
    public final static String BLANK = " ";
    
    /**
     * 0 长度的字符串
     */
    public static final String EMPTY = "";

    /**
     * 将输入的字节进行 Base64 编码，然后再转换为指定字符集形式的字符串。
     * @param bytes  输入字节数组，如果为空，返回长度为 0 的字符串。
     * @param charsetName 字符集，如果未传递字符集，则使用默认字符集。
     * @return 经编码后的字符串
     */
    static public String base64Encode(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return EMPTY;
        }
        // 如果未传递字符集，则使用默认字符集。
        if (charsetName == null) {
            charsetName = DEFAULT_CHARSET_NAME;
        }
        byte[] bs = Base64.encodeBase64(bytes);
        try {
            return new String(bs, charsetName);
        } catch (UnsupportedEncodingException ex) {
            throw new SystemException(String.format("系统不支持此字符集[%s]。", charsetName), ex);
        }
    }

    /**
     * 将输入的字符串转换为指定字符集形式的字节数组，然后进行 Base64 解码。即执行"base64Encode"的反过程。
     * @param bytes  输入字符串，如果为空，返回长度为 0 的字节数组。
     * @param charsetName 字符集，如果未传递字符集，则使用默认字符集。
     * @return 经解码后的字符串
     */
    static public byte[] base64Decode(String str, String charsetName) {
        if (str == null) {
            return new byte[0];
        }
        // 如果未传递字符集，则使用默认字符集。
        if (charsetName == null) {
            charsetName = DEFAULT_CHARSET_NAME;
        }

        try {
            byte[] bytes = Base64.decodeBase64(str.getBytes(charsetName));
            return bytes;
        } catch (UnsupportedEncodingException ex) {
            throw new SystemException(String.format("系统不支持此字符集[%s]。", charsetName), ex);
        }
    }

    /**
     * 将空（null）转换成长度为0的字符串，如果传入的字符串不为空，则返回传入的字符串，不作任何处理。
     * @param str 可能是空（null）也可能是字符串
     * @return 不为空的字符串
     */
    public static String nullString (String str)
    {
        if (str == null)
        {
            return EMPTY;
        }
        return str;
    }
    
    /**
     * 由骆驼记法转换为下划线分割记法（数据库中常常采用的方式）
	 * 默认的映射规则是：在大写字符字母前面(除了第一个字符)填写下划线,然后将全部字符转换为大写。	
     * @param str 被转换的字符
     * @return 如果为空，则抛出异常
     */
    public static String camel2Underline (String str) {
    	if (str == null) {
		    throw new BadParameterException("映射的属性名称不能为空！");
		}
		StringBuffer columnName = new StringBuffer();
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i); 
			if (i != 0 && c <= 'Z' && c >= 'A') {
				columnName.append('_');
			}
			columnName.append(c);
		}
		return columnName.toString().toUpperCase();
    }
    
    /**
     * 由下划线分割记法转换为骆驼记法（Java常常采用的习惯记法）
	 * 默认的映射规则是：去掉下划线，并将下划线后面的字母改为大写字符,其余的全部字符转换为小写。	
     * @param str 被转换的字符
     * @return 如果为空，则抛出异常
     */
    public static String underline2Camel(String str) {
		if (str == null) {
			throw new BadParameterException("映射的属性名称不能为空！");
		}
		boolean isUnderline = false; // 前一个字符是否是下划线
		StringBuffer newStr = new StringBuffer();
		int distance = 'a' - 'A'; // 大小写之间的距离

		// 逐个处理字符
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			//首字母要大写
			if (i == 0) {
				if (c <= 'z' && c >= 'a') {
				    newStr.append(c - distance);
				} else {
					newStr.append(c);
				}
			} else if ('_' == c) {
				// 如果是下划线，做好标记继续处理
				isUnderline = true;
			} else {
				// 前一个字符是下划线,并且当前字符是小写字母，则将其转换为大写字母
				if (isUnderline && c <= 'z' && c >= 'a') {
					newStr.append((char) (c - distance));
					// 前一个字符不是下划线,并且当前字符是大写字母，
					// 则将其转换为小写字母（除了下划线后面的字母，全部转为小写）
				} else if (!isUnderline && c <= 'Z' && c >= 'A') {
					newStr.append((char) (c + distance));
				} else {
					newStr.append(c);
				}
				isUnderline = false;
			}
		}
		return newStr.toString();
	}

    /**
     * 将源字符串数组拷贝到目的字符串数组中。两个数组都不能未空，目的字符串数组的长度一定要大于源字
     * 符串数组，否则返回假
     * @param dest 目的字符串数组
     * @param source 源字符串数组
     * @return 拷贝是否成功
     */
    public static boolean copy (String[] dest, String[] source)
    {
        boolean success = false;
        if (dest != null && source != null && source.length <= dest.length)
        {
            for (int i = 0; i < source.length; i++)
            {
                dest[i] = source[i];
            }
            success = true;
        }
        return success;
    }

    /**
     * 将输入的字符串的首字母大写
     * @param str 字符串
     * @return 将输入的字符串的首字母大写，如果输入为空，或者时字符串长度为0，则返回空。
     */
    public static String capitalize (String str)
    {
        String newStr = null;
        if (str != null && str.length() > 0)
        {
            newStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return newStr;
    }

    /**
     * 将字符串列表转换成字符串数组
     * @param list List 字符串列表
     * @return String[]
     */
    public static String[] list2Array (List list)
    {
        String[] strs = new String[list.size()];
        for (int i = 0; i < strs.length; i++)
        {
            strs[i] = (String)list.get(i);
        }
        return strs;
    }
    
    /**
     * 将字符串数组拼装成以指定分隔符分割的字符串形式。如字符串数组为["str1", "str2", "str3"],
     * 分隔符为“,”,则形成的字符串为"str1,str2,str3"；(放入工具类)
     * @param strs 字符串数组。 
     * @param delimeter
     * @return
     */
    public static String join (String[] strs, String delimeter) {
        //输入参数不能为空，否则返回空
        if (strs == null || delimeter == null) {
            return null;
        }
        
        StringBuffer strBuf = new StringBuffer();
        String _deli = EMPTY;  //分隔符，第一次拼装时不进行拼装
        for (int i = 0; i < strs.length; i++) {
            strBuf.append(_deli);
            strBuf.append(strs[i]);
            _deli = delimeter;
        }
        return strBuf.toString();
    }
    
    /**
     * 将字符串数组拼装成以指定分隔符分割的字符串形式。如字符串数组为["str1", "str2", "str3"],
     * 分隔符为“,”,则形成的字符串为"str1,str2,str3"；(放入工具类)
     * @param strs 字符串数组。 
     * @param delimeter
     * @return
     */
    public static String join (Collection col, String delimeter) {
        //输入参数不能为空，否则返回空
        if (col == null || delimeter == null) {
            return null;
        }
        
        StringBuffer strBuf = new StringBuffer();
        String _deli = EMPTY;  //分隔符，第一次拼装时不进行拼装
        for (Iterator iter = col.iterator(); iter.hasNext(); ) {
            strBuf.append(_deli);
            strBuf.append(iter.next().toString());
            _deli = delimeter;
        }
        return strBuf.toString();
    }
    
//    /**
//     * 根据传入的参数替换字符串中的变量，方便观察结果。如传入字符串”Nice to see you, ${name}!,
//     *  I'm ${visitor}, etc. ${visitor}。${null}“，传入变量{name="sss",visitor="bdo"},
//     *  输出字符串为：”Nice to see you, sss!, I'm bdo, etc. bdo。${null}“；变量的替换以
//     *  Map参数的主键为参考，如果未传入相应的参数，则不进行替换。
//     * @param str 待转换的字符串
//     * @param params 参数 
//     * @pre-condition str != null && params != null
//     * @return 转换后的字符串
//     */
//    public static String replace (String str, Map params) 
//    throws Exception{
//        StringWriter sw = new StringWriter();
//        StringReader sr = new StringReader(str);
//        VelocityContext context = new VelocityContext();
//        //注入参数
//        for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
//            String paramName = (String) iter.next();
//            context.put(paramName, params.get(paramName));    
//        }
//        
//        VelocityEngine ve = new VelocityEngine();
//        ve.init(new Properties());        
//        ve.evaluate(context, sw, "velocity", sr);
//        return sw.toString();
//    }

    /**
     * 将字符串数组转换成字符串列表
     * @param array String[] array 字符串数组
     * @return List 字符串列表
     */
    public static List array2List (String[] array)
    {
        List list = null;
        if (array != null)
        {
            list = new ArrayList(array.length);
            for (int i = 0; i < array.length; i++)
            {
                list.add(list.get(i));
            }
        }
        return list;
    }

    /**
     * 将字符串的开头转换成字母小写
     * @param str 字符串
     * @return
     */
    public static String lowcaseCapital (String str)
    {
        if (str != null)
        {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
        return null;
    }

    /**
     * 将字符串的开头转换成字母小写
     * @param str 字符串
     * @return
     */
    public static String uppercaseCapital (String str)
    {
        if (str != null)
        {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return null;
    }


    public static String replaceFirst (String str, String original, String replacement) {
        String newStr = null;
        if (str != null && replacement != null && str.indexOf(original) > -1) {
            int index = str.indexOf(original);
            newStr = str.substring(0, index) + replacement
                     + str.substring(index + original.length());
        } else {
            newStr = str;
        }
        return newStr;
    }

    /**
     * 得到分割字符的前半部分,不包含分割字符本身,如果未发现分割字符,返回原字符串
     * @param str String 字符串
     * @param delim char 分割字符
     * @return String 分割字符的前半部分
     */
    public static String getFront (String str, char delim) {
        String ret = null;
        if (str != null && str.indexOf(String.valueOf(delim)) >= -1) {
            int index = str.indexOf(String.valueOf(delim));
            ret = str.substring(0, index);
        }
        return ret;
    }

    /**
     * 将一个对象转换层其对应的字符串表示形式。
     * @param obj Object 待转换的对象
     * @return String 对象的字符串表示形式
     */
    public static String toString(Object obj) {
        return null;
    }
    
    /**
     * 判断一个字符串是否为真。如果字符串不为空且是“true”，“yes”，“y”，或者，
     * “1”时，表示真；其余的情况表示假。
     * @param str
     * @return
     */
    public static boolean isTrue (String str) {
    	return (str != null && ("y".equalsIgnoreCase(str) 
				|| "yes".equalsIgnoreCase(str) || "1".equalsIgnoreCase(str) 
				|| "true".equalsIgnoreCase(str)));
    }
    
    /**
     * 如果第一个字符串不为空，优先返回第一个字符串，否则返回第二个字符串。
     * @param str1
     * @param str2
     * @return 
     */
    public static String priority (String str1, String str2) {
    	return (str1 != null ? str1 : str2);
    }
    
    /**
	 * 从异常中抽取出指定行数的信息
	 * 
	 * @param t
	 * @param lineCount
	 * @return 返回非空的异常信息字符串
	 */
	static public String extract(Throwable t, int lineCount) {
		if (t == null) {
			return EMPTY;
		}
		Throwable current = t;
		StringBuilder strBld = new StringBuilder();
		// 10 是最大 的嵌套层次
		for (int i = 0; (i < 10 && current != null); i++) {
			readLines(strBld, current, lineCount);
			current = current.getCause();
		}
		return strBld.toString();
	}

	static private void readLines(final StringBuilder strBld, Throwable t,
			int lineCount) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		Reader reader = new StringReader(sw.toString());
		LineNumberReader lnr = new LineNumberReader(reader);
		try {
			String line = lnr.readLine();
			int i = 0;
			while (i < lineCount && line != null) {
				if (line.trim().length() > 0) {
					strBld.append(line).append("\n");
					i++;
				}
				line = lnr.readLine();

			}
		} catch (IOException e) {
			// do nothing...
		}
	}
}
