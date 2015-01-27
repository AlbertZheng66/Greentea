
package com.xt.core.json.parse.impl;

import com.xt.core.exception.SystemException;
import com.xt.core.json.parse.Parsable;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class EnumParser implements Parsable {

    private final Logger logger = Logger.getLogger(EnumParser.class);

    public Object parse(Object obj) {
        if (!(obj instanceof JSONObject)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        // {"__className":"java.lang.Enum","name":"REGISTERED","ordinal":0,"title":"REGISTERED","className":"com.xt.bcloud.app.AppVersionState"}
        JSONObject jsonObj = (JSONObject) obj;
        String className = jsonObj.getString("className");

        if (StringUtils.isEmpty(className)) {
            throw new SystemException(String.format("枚举类型[%s]的属性[className]不能为空。", jsonObj));
        }
        // {"__className":"java.lang.Enum","name":"REGISTERED","ordinal":0,"title":"REGISTERED","className":"com.xt.bcloud.app.AppVersionState"}
        Class _enum;
        try {
            _enum = Class.forName(className);
            String name = jsonObj.getString("name");
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return Enum.valueOf(_enum, name);
        } catch (ClassNotFoundException ex) {
            throw new SystemException(String.format("未找到枚举类型[%s]的定义。", className), ex);
        }
    }
}
