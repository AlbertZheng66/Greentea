package com.xt.core.json;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.exception.SystemException;
import com.xt.core.json.parse.Parsable;
import com.xt.core.json.parse.ParserFactory;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.IOHelper;
import com.xt.core.utils.Pair;
import com.xt.core.utils.dic.Dictionary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 注意循环引用的问题尚待处理。
 *
 * @author albert
 */
public class JsonBuilder2 {

    public static final String PARAM_VALUE = "value";
    private final Logger logger = Logger.getLogger(JsonBuilder.class);
    public static final String PARAM__CLASS_NAME = "__className";
    public static final String PARAM_ID = "__id";
    public static final String PARAM_REF_ID = "__refId";
    public static final String PARAM_ORI_CLASS_NAME = "__originalClassName";
    private transient final static Object NULL_OBJ = "_null_";
    private final Map<Object, String> referencedObjects = new HashMap();
    private final Map<String, Object> dereferencedObjects = new HashMap();
    private int refId = 0;

    public JsonBuilder2() {
    }

    public String build(Serializable serializable) {
        Object obj = buildObject(serializable);
        if (obj == NULL_OBJ) {
            return null;
        }
        if (obj instanceof JSON) {
            return ((JSON) obj).toString();
        } else {
            JSONObject jsonObj = new JSONObject();
            jsonObj.element("value", obj);
            return jsonObj.toString();
        }
    }

    private Object buildObject(Serializable value) {
        if (value == null) {
            return JSONNull.getInstance();
        }

        Class valueClass = value.getClass();

        if (Modifier.isTransient(valueClass.getModifiers())) {
            return NULL_OBJ;
        }

        // 处理原始类型
        if (valueClass.isPrimitive() || value instanceof Number
                || value instanceof Character || value instanceof String || value instanceof Boolean) {
            return value;
        }

        //  在此处理循环引用的问题
        if (referencedObjects.containsKey(value)) {
            JSONObject ref = new JSONObject();
            ref.element(PARAM__CLASS_NAME, RefObject.class.getName());
            ref.element(PARAM_REF_ID, referencedObjects.get(value));
            return ref;
        }
        JSON ret = null;
        if (valueClass == byte[].class) {
            // 处理字节数组
            ret = processInputStream(value.getClass(), new ByteArrayInputStream((byte[]) value));
        } else if (Date.class.isAssignableFrom(valueClass) || Calendar.class.isAssignableFrom(valueClass)) {
            // 处理日期类型
            Converter conv = ConverterFactory.getInstance().getConverter(valueClass, String.class);
            String _value = (String) conv.convert(valueClass, String.class, value);
            JSONObject _josnDate = new JSONObject();
            _josnDate.element(PARAM__CLASS_NAME, Date.class.getName());
            _josnDate.element(PARAM_ORI_CLASS_NAME, valueClass.getName());
            _josnDate.element(PARAM_VALUE, _value);
            ret = _josnDate;
        } else if (value instanceof Collection) {
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
                if (obj == null) {
                    array.add(null);
                } else if (obj instanceof Serializable) {
                    Object child = buildObject((Serializable) obj);
                    array.element(child);
                } else {
                    array.add(null);
                }
            }
            ret = array;
        } else if (value instanceof Map) {
            JSONObject map = new JSONObject();
            for (Object obj : ((Map) value).entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                if (entry.getValue() != null && entry.getValue() instanceof Serializable) {
                    Object child = buildObject((Serializable) entry.getValue());
                    if (obj != NULL_OBJ) {
                        map.element(entry.getKey().toString(), child);
                    }
                }
            }
            ret = map;
        } else if (value instanceof Enum) {
            Enum _enum = (Enum) value;
            JSONObject jsonEnum = new JSONObject();
            jsonEnum.element(PARAM__CLASS_NAME, Enum.class.getName());
            jsonEnum.element("name", _enum.name());
            jsonEnum.element("ordinal", _enum.ordinal());
            jsonEnum.element("title", _enum.toString());
            jsonEnum.element("className", value.getClass().getName());
            ret = jsonEnum;
        } else if (value instanceof Dictionary) {
            Dictionary dic = (Dictionary) value;
            JSONObject jsonDic = new JSONObject();
            jsonDic.element(PARAM__CLASS_NAME, value.getClass().getName());
            jsonDic.element("name", String.valueOf(dic.getName()));
            jsonDic.element("items", dic.getItems());
            ret = jsonDic;
        } else if (value instanceof InputStream) {
            InputStream inputStream = (InputStream) value;
            JSONObject isElem = processInputStream(value.getClass(), inputStream);
            ret = isElem;
        } else if (value instanceof Class) {
            Class clazz = (Class) value;
            JSONObject isElem = new JSONObject();
            isElem.element(PARAM__CLASS_NAME, value.getClass().getName());
            isElem.element("name", clazz.getName());
            ret = isElem;
        } else {
            // 只有类类型处理引用ID
            String currentRefId = String.valueOf(refId++);
            referencedObjects.put(value, currentRefId);
            JSONObject jsonObj = new JSONObject();
            jsonObj.element(PARAM_ID, currentRefId);
            jsonObj.element(PARAM__CLASS_NAME, value.getClass().getName());
            Pair[] pairs = BeanHelper.getProperties(value);
            for (Pair pair : pairs) {
                Object _value = pair.getValue();
                if (_value != null && _value instanceof Serializable) {
                    Field field = ClassHelper.getField(valueClass, pair.getName());
                    if (field != null && Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                    Object obj = buildObject((Serializable) _value);
                    if (obj != NULL_OBJ) {
                        jsonObj.element(pair.getName(), obj);
                    }
                }
            }
            ret = jsonObj;
        }
        return ret;
    }

    private Object processBytes(JSONObject _jsonObj) {
        Object ret;
        String _bytesValue = _jsonObj.getString(PARAM_VALUE);
        if (StringUtils.isEmpty(_bytesValue)) {
            ret = new byte[0];
        } else {
            ret = Base64.decodeBase64(_bytesValue);
        }
        return ret;
    }

    private Class processClass(JSONObject _jsonObj, Object ret) throws SystemException {
        String clazzName = _jsonObj.getString("name");
        if (StringUtils.isNotEmpty(clazzName)) {
            return forName(clazzName);
        }
        return null;
    }

    private Object processDate(JSONObject _jsonObj) {
        Object ret;
        String _dateValue = _jsonObj.getString(PARAM_VALUE);
        String oriClassName = _jsonObj.getString(PARAM_ORI_CLASS_NAME);
        Class targetClass = ClassHelper.getClass(oriClassName);
        Converter conv = ConverterFactory.getInstance().getConverter(String.class, targetClass);
        ret = conv.convert(String.class, targetClass, _dateValue);
        return ret;
    }

    private JSONObject processInputStream(Class valueClass, InputStream inputStream) throws SystemException {
        JSONObject isElem = new JSONObject();
        isElem.element(PARAM__CLASS_NAME, valueClass.getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHelper.i2o(inputStream, baos, true, true);
        String encodedValue = Base64.encodeBase64String(baos.toByteArray());
        isElem.element(PARAM_VALUE, encodedValue);
        return isElem;
    }

    public boolean isJsonString(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        String trimed = string.trim();
        if (!trimed.startsWith("{") || !trimed.endsWith("}")) {
            return false;
        }
        return true;
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

                // 
                if (RefObject.class.getName().equals(className)) {
                    String _refId = _jsonObj.optString(PARAM_REF_ID);
                    if (StringUtils.isNotEmpty(_refId)) {
                        ret = dereferencedObjects.get(_refId);  // 一定要保证先解析。
                    }
                } else if (className.startsWith("[")) {
                    // 处理数组类型
                    if (className.equals(byte[].class.getName())) {
                        ret = processBytes(_jsonObj);
                    } else {
                        // 暂时未支持其他类型的数组
                        ret = null;
                    }
                } else if (className.equals(Class.class.getName())) {
                    ret = processClass(_jsonObj, ret);
                } else if (Date.class.getName().equals(className)) {
                    ret = processDate(_jsonObj);
                } else {
                    Parsable parsable = ParserFactory.getInstance().getParsable(className);
                    if (parsable != null) {
                        // 自定义解析方式
                        ret = parsable.parse(_jsonObj);
                    } else {
                        ret = processObject(className, _jsonObj);
                    }
                }

            } else {
                ret = processMap(_jsonObj);
            }
        }
        return ret;
    }

    private Object processMap(JSONObject _jsonObj) {
        Object ret;
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
        return ret;
    }

    private Object processObject(String className, JSONObject _jsonObj) {
        Object obj = ClassHelper.newInstance(className);
        String id = _jsonObj.optString(PARAM_ID);
        if (StringUtils.isNotEmpty(id)) {
            dereferencedObjects.put(id, obj);
        }
        for (Iterator<String> iter = _jsonObj.keys(); iter.hasNext();) {
            String key = iter.next();
            // 忽略关键字
            if (PARAM__CLASS_NAME.equals(key) || PARAM_ID.equals(key) || PARAM_ORI_CLASS_NAME.equals(key)) {
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
        return obj;
    }

    private Class forName(String clazzName) throws SystemException {
        Class ret = null;


        try {
            if ("void".equals(clazzName)) {
                return Void.class;
            } else if ("boolean".equals(clazzName)) {
                return boolean.class;
            } else if ("int".equals(clazzName)) {
                return int.class;
            } else if ("float".equals(clazzName)) {
                return float.class;
            } else if ("long".equals(clazzName)) {
                return long.class;
            } else if ("double".equals(clazzName)) {
                return double.class;
            } else if ("char".equals(clazzName)) {
                return char.class;
            } else if ("byte".equals(clazzName)) {
                return byte.class;
            } else {
                ret = Class.forName(clazzName);
            }
        } catch (ClassNotFoundException ex) {
            throw new SystemException(String.format("类[%s]不存在。", clazzName), ex);
        }
        return ret;
    }
}
