
package com.xt.core.conv.impl;

import com.xt.core.conv.Converter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.Pair;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author albert
 */
public class ObjectMapConverter implements Converter<Object, Map> {

    public ObjectMapConverter() {
    }

    public Map convert(Class<Object> sourceClass, Class<Map> targetClass, Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        Map ret = new HashMap();
        Pair[] pairs = BeanHelper.getProperties(value);
        for (Pair pair : pairs) {
            if (pair.getValue() != null) {
                ret.put(pair.getName(), pair.getValue());
            }
        }
        return ret;
    }


}