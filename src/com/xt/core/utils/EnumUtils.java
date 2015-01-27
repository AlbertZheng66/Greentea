

package com.xt.core.utils;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;

/**
 *
 * @author albert
 */
public class EnumUtils {

    static public String toString(Enum _enum) {
        if (_enum == null) {
            return null;
        }
        Converter  converter = ConverterFactory.getInstance().getConverter(_enum.getClass(), String.class);
        return (String)converter.convert(_enum.getClass(), String.class, _enum);
    }

}
