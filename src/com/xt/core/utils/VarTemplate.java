package com.xt.core.utils;

import com.xt.core.exception.SystemException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author albert
 */
public class VarTemplate {

    /**
     * 变量定义的模式。注意：必须对变量的名称进行限定，否则后面的替换有问题 备用： "\\$\\{[.-}]+\\}";
     * 变量可以定义除了“}”以外的任何名字
     */
    public static final String VAR_PATTERN = "\\$\\{[_a-zA-Z][_a-zA-Z0-9\\.]*\\}";

    /**
     * 用一个对象的值格式化此模板的值。属性的定义方式为：
     * 替换掉字符串中的变量（直接替换，暂不做转义处理）。注意：如果被替换的值里面出现合法的变量名称，如：“${aa}”，将出现死循环
     *
     * @param str 被替换的字符串
     * @param params 参数对象
     * @return 替换后的字符串，如果为空，则返回空
     * @param pattern
     * @param source
     * @return
     */
    static public String format(String str, Map<String, Object> values, boolean lenient) {
        values = values == null ? Collections.EMPTY_MAP : values;
        Set<String> varNames = getVarNames(str);
        String ret = str;
        for (String varName : varNames) {
            Object _value = values.get(varName);
            String value = _value == null ? null : _value.toString();

            if (value == null) {
                if (lenient) {
                    continue;
                } else {
                    throw new SystemException(String.format("变量[%s]对应的值未定义（当前参数[%s]）。", varName, values));
                }
            }
            ret = format(ret, varName, value);
        }

        return ret;
    }

    static public String formatMap(String str, Map<String, ? extends Object> values) {
        return format(str, values, false);
    }

    static public String format(String str, Object obj) {
        return format(str, obj, false);
    }

    /**
     * 用一个对象对字符串进行格式化。
     *
     * @param str
     * @param obj
     * @return
     */
    static public String format(String str, Object obj, boolean lenient) {
        final Map<String, Object> values = new HashMap();

        if (obj != null) {
            if (obj instanceof Map) {
                values.putAll((Map)obj);
            } else {
                Pair[] props = BeanHelper.getProperties(obj);
                for (Pair pair : props) {
                    values.put(pair.getName(), pair.getValue());
                }
            }
        }

        return format(str, values, lenient);
    }

    static public String format(String str, String name, String value) {
        if (str == null) {
            return str;
        }
        if (name == null || value == null) {
            throw new SystemException("参数名称及被替换的值都不能为空。");
        }
        Pattern p = Pattern.compile(VAR_PATTERN);

        Matcher m = p.matcher(str);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String varName = str.substring(start + 2, end - 1);
            if (!name.equals(varName)) {
                continue;
            }

            // // 替换变量，使用String的非正则表达式版本，否则在传入类似（“c:\temp\instanc.xml”）的字符串时出现问题
            //            str = str.replace("${" + varName + "}", value);
            StringBuffer temp = new StringBuffer();
            if (start > 0) {
                temp.append(str.substring(0, start));
            }
            temp.append(value);
            if (end < (str.length())) {
                temp.append(str.substring(end, str.length()));
            }
            str = temp.toString();

            m = p.matcher(str); // 字符串已经变化，一定要重新替换
        }
        return str;
    }

    /**
     * 返回指定字符串包含的所有变量。
     *
     * @param str 字符串，如果为空，不进行任何处理
     * @return 变量名称列表，如果不存在任何变量名称，则返回长度为 0 的集合。
     */
    static public Set<String> getVarNames(String str) {
        if (str == null) {
            return Collections.EMPTY_SET;
        }
        Set<String> names = new HashSet();
        Pattern p = Pattern.compile(VAR_PATTERN);
        Matcher m = p.matcher(str);
        int startIndex = 0;
        while (m.find(startIndex)) {
            int start = m.start();
            int end = m.end();
            startIndex = end;
            String varName = str.substring(start + 2, end - 1);
            names.add(varName);
        }
        return names;
    }

    /**
     * 判断指定字符串是否包含变量。
     *
     * @param str 字符串，
     * @return 如果字符串为空，或者不包含变量模板，则返回 false，否则返回 true。
     */
    static public boolean contains(String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile(VAR_PATTERN);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
