package com.xt.core.json;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.json.parse.Parsable;
import com.xt.core.json.parse.ParserFactory;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.Pair;
import com.xt.core.utils.dic.Dictionary;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 注意循环引用的问题尚待处理。
 *
 * @author albert
 */
public class JsonBuilder {

    private final Logger logger = Logger.getLogger(JsonBuilder.class);
    public static final String PARAM__CLASS_NAME = "__className";

    /**
     * 使用精简模式输出JSON语句，即清除类相关信息
     */
    private boolean concised = false;

    public JsonBuilder() {
    }

//    private final Map<Object, String> referencedObject = new HashMap();
//    private int refId = 0;
    public String build(Serializable serializable) {
        Object obj = buildObject(serializable);
        if (obj instanceof JSON) {
            return ((JSON) obj).toString();
        } else {
            JSONObject jsonObj = new JSONObject();
            jsonObj.element("value", obj);
            // buildObject(jsonObj, serializable);
            return jsonObj.toString();
        }
    }

    private Object buildObject(Serializable value) {
        if (value == null) {
            return JSONNull.getInstance();
        }

        Class valueClass = value.getClass();
        // 处理原始类型
        if (valueClass.isPrimitive() || value instanceof Number || value instanceof Character || value instanceof String || value instanceof Boolean) {
            return value;
        }

        // 处理日期类型
        if (Date.class.isAssignableFrom(valueClass) || Calendar.class.isAssignableFrom(valueClass)) {
            Converter conv = ConverterFactory.getInstance().getConverter(valueClass, String.class);
            return conv.convert(valueClass, String.class, value);
        }

        //  在此处理循环引用的问题
//        if (referencedObject.containsKey(value)) {
//             JSONObject map = new JSONObject();
//             map.element("refId", referencedObject.get(value));
//             return map;
//        }
        Object ret = null;
//        int currentRefId = refId++;
//        referencedObject.put(value, String.valueOf(currentRefId));

        if (value instanceof Collection) {
            JSONArray array = new JSONArray();
            for (Object obj : (Collection) value) {
                if (obj instanceof Serializable) {
                    Object child = buildObject((Serializable) obj);
                    array.element(child);
                } else {
                    logger.warn(String.format("类[%s]不可序列化，未输出。", obj));
                }
            }
            ret = array;
        } else if (value.getClass().isArray()) {
            // 处理数组
            JSONArray array = new JSONArray();
            for (Object obj : (Object[]) value) {
                if (obj instanceof Serializable) {
                    Object child = buildObject((Serializable) obj);
                    array.element(child);
                }
            }
            ret = array;
        } else if (value instanceof Map) {
            JSONObject map = new JSONObject();
            for (Object obj : ((Map) value).entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                if (entry.getValue() != null && entry.getValue() instanceof Serializable) {
                    Object child = buildObject((Serializable) entry.getValue());
                    map.element(entry.getKey().toString(), child);
                }
            }
            ret = map;
        } else if (value instanceof Enum) {
            Enum _enum = (Enum) value;
            if (concised) {
                ret = _enum.name();
            } else {
                JSONObject jsonEnum = new JSONObject();
                jsonEnum.element(PARAM__CLASS_NAME, Enum.class.getName());
                jsonEnum.element("name", _enum.name());
                jsonEnum.element("ordinal", _enum.ordinal());
                jsonEnum.element("title", _enum.toString());
                jsonEnum.element("className", value.getClass().getName());
                ret = jsonEnum;
            }
        } else if (value instanceof Dictionary) {
            Dictionary dic = (Dictionary) value;
            JSONObject jsonDic = new JSONObject();
            jsonDic.element(PARAM__CLASS_NAME, value.getClass().getName());
            jsonDic.element("name", dic.getName());
            jsonDic.element("items", dic.getItems());
            ret = jsonDic;
        } else {
            JSONObject jsonObj = new JSONObject();
            if (!concised) {
                jsonObj.element(PARAM__CLASS_NAME, value.getClass().getName());
            }
            Pair[] pairs = BeanHelper.getProperties(value);
            for (Pair pair : pairs) {
                Object _value = pair.getValue();
                if (_value != null && _value instanceof Serializable) {
                    jsonObj.element(pair.getName(), buildObject((Serializable) _value));
                }
            }
            ret = jsonObj;
        }
        return ret;
    }

    public Object parse(String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(string);
        return parse(jsonObject);
    }

    public Object parse(JSON json) {
        if (json == null || json instanceof JSONNull) {
            return null;
        }
        Object ret = null;
        if (json instanceof JSONArray) {
            Object[] array = ((JSONArray) json).toArray();
            if (array == null || array.length == 0) {
                ret = new ArrayList(1);
            } else {
                List objs = new ArrayList(array.length);
                for (Object obj : array) {
                    if (obj instanceof JSON) {
                        obj = parse((JSON) obj);
                    }
                    objs.add(obj);
                }
                ret = objs;
            }
        } else if (json instanceof JSONObject) {
            JSONObject _jsonObj = (JSONObject) json;
            if (_jsonObj.isNullObject()) {
                ret = null;
            } else if (_jsonObj.containsKey(PARAM__CLASS_NAME)) {
                // 作为实体对象进行处理
                String className = _jsonObj.getString(PARAM__CLASS_NAME);

                // TODO: 如果类是数组???
                if (className.startsWith("[")) {
                    ret = null;
                } else {
                    Parsable parsable = ParserFactory.getInstance().getParsable(className);
                    if (parsable != null) {
                        // 自定义解析方式
                        ret = parsable.parse(_jsonObj);
                    } else {
                        Object obj = ClassHelper.newInstance(className);
                        for (Iterator<String> iter = _jsonObj.keys(); iter.hasNext();) {
                            String key = iter.next();
                            // 忽略关键字
                            if (PARAM__CLASS_NAME.equals(key)) {
                                continue;
                            }
                            Object _value = _jsonObj.get(key);
                            Class fieldType = ClassHelper.getFieldType(obj.getClass(), key);
                            // 如果目标域是字符串类型，则不进行转换(注意：有可能在字符串类型中存储JSON字符串，此时不需要解析)
                            if (String.class.equals(fieldType)) {
                                _value = _value.toString();
                            } else if (_value instanceof JSON) {
                                _value = parse((JSON) _value);
                            }
                            BeanHelper.copyProperty(obj, key, _value);
                        }
                        ret = obj;
                    }
                }
            } else {
                // 作为 Map 对象进行处理
                Map map = new HashMap();
                for (Iterator<String> iter = _jsonObj.keys(); iter.hasNext();) {
                    String key = iter.next();
                    Object _value = _jsonObj.get(key);
                    if (_value instanceof JSON) {
                        _value = parse((JSON) _value);
                    }
                    map.put(key, _value);
                }
                ret = map;
            }
        }
        return ret;
    }

    public boolean isConcised() {
        return concised;
    }

    public void setConcised(boolean concised) {
        this.concised = concised;
    }

}
