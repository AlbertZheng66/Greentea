

package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;
import com.xt.core.utils.SqlUtils;
import java.sql.Clob;

/**
 * 用于 Clob 与字符串之间进行转换的处理器。Clob 只能完成单向转换，即从Clob中读取数据的转换。
 * @author albert
 */
public class ClobConverter implements BaseConverter<Clob> {

    public Class<Clob> getConvertedClass() {
        return Clob.class;
    }

    public Clob convert(String value) {
        if (value == null) {
            return null;
        }
        throw new UnsupportedOperationException("不支持用此方法进行转换。");
    }

    public String convertToString(Clob clob) {
        if (clob == null) {
            return null;
        }

        char[] chars = SqlUtils.readClob(clob, char[].class);
        return new String(chars);
    }

}

