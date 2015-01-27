
package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;

/**
 *
 * @author albert
 */
public class CharsConverter implements BaseConverter<char[]> {

    public Class<char[]> getConvertedClass() {
        return char[].class;
    }

    public char[] convert(String value) {
        if (value == null) {
            return null;
        }
        return value.toCharArray();
    }

    public String convertToString(char[] chars) {
        if (chars == null) {
            return null;
        }
        return new String(chars);
    }
}
