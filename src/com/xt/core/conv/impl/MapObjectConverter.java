
package com.xt.core.conv.impl;

import com.xt.core.conv.Converter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class MapObjectConverter  implements Converter<Map, Object> {

   private final Logger logger = Logger.getLogger(MapObjectConverter.class);

    public Object convert(Class<Map> sourceClass, Class<Object> targetClass, Map value) {
        if (value == null) {
            return null;
        }
        Object ret = ClassHelper.newInstance(targetClass);
        for(Object obj : value.entrySet()) {
            Map.Entry entry = (Map.Entry)obj;
            String propertyName = entry.getKey().toString();
            if (BeanHelper.hasProperty(ret, propertyName)) {
                // logger.debug("ret=" + ret + ";propertyName=" + propertyName + ";entry.getValue()=" + (entry.getValue() == null ? null : entry.getValue().getClass()) );
                BeanHelper.copyProperty(ret, propertyName, entry.getValue());
            } else {
                logger.warn(String.format("类[%s]中未发现属性[%s]。", targetClass.getName(), propertyName));
            }
        }
        return ret;
    }


}
