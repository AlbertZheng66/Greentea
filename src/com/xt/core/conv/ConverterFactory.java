package com.xt.core.conv;

import com.xt.core.conv.impl.BlobConverter;
import com.xt.core.conv.impl.ByteArrayConverter;
import com.xt.core.conv.impl.CharsConverter;
import com.xt.core.conv.impl.ClobConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xt.core.conv.impl.EnumIntConverter;
import com.xt.core.conv.impl.EnumStringConverter;
import com.xt.core.conv.impl.InputStreamConverter;
import com.xt.core.conv.impl.IntEnumConverter;
import com.xt.core.conv.impl.MapObjectConverter;
import com.xt.core.conv.impl.ObjectMapConverter;
import com.xt.core.conv.impl.ReaderConverter;
import com.xt.core.conv.impl.StringEnumConverter;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.DateUtils;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class ConverterFactory {
    public static final String DEFAULT_LONG_PATTERN = "^((\\+|\\-)?([1-9]\\d{0,17})|0)$";

    /**
     * 建立一个通用转换（从字符串数据到指定类型）的映射关系。 这种通用转换以字符串为基础，即首先将源数据转换为字符串， 然后再将字符串转换为目标类型。
     */
    private final Map<Class, BaseConverter> commonConverterMap = new HashMap<Class, BaseConverter>();
    /**
     * 建立类型之间的直接转换关系。直接转换关系可以处理继承关系。
     */
    @SuppressWarnings("unchecked")
    private final Map<Key, Converter> directConverterMap = new HashMap<Key, Converter>();
    
    private final static ConverterFactory instance = new ConverterFactory();

    static {
        // 枚举类型和字符串、整数之间进行转换
        instance.register(Enum.class, String.class, new EnumStringConverter());
        instance.register(Enum.class, Integer.class, new EnumIntConverter());
        instance.register(Integer.class, Enum.class, new IntEnumConverter());
        instance.register(String.class, Enum.class, new StringEnumConverter());

        // 对象和 Map 之间进行转换
        instance.register(Object.class, Map.class, new ObjectMapConverter());
        instance.register(Map.class, Object.class, new MapObjectConverter());
    }

    private ConverterFactory() {
    }

    public static ConverterFactory getInstance() {
        return instance;
    }

    synchronized public <T> void register(BaseConverter<T> converter) {
        if (converter != null) {
            Class<T> convertedClass = converter.getConvertedClass();
            commonConverterMap.put(convertedClass, converter);
            // 包装类要同时处理其对应的原生类型
            if (ClassHelper.getPrimitive(convertedClass) != convertedClass) {
                commonConverterMap.put(ClassHelper.getPrimitive(convertedClass),
                        converter);
            }
        }
    }

    /**
     * 注册直接转换接口
     *
     * @param <S>
     * @param <T>
     * @param sourceClass
     * @param targetClass
     * @param converter
     */
    synchronized public <S, T> void register(Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter) {
        if (sourceClass != null && targetClass != null && converter != null) {
            Key key = new Key(sourceClass, targetClass);
            directConverterMap.put(key, converter);
        }
    }

    @SuppressWarnings("unchecked")
    synchronized public <S, T> Converter<S, T> getConverter(
            Class<S> sourceClass, Class<T> targetClass) {
        if (sourceClass == null || targetClass == null) {
            throw new ConvertException(String.format("源类型[%s]和目标类[%s]型都不能为空。", sourceClass, targetClass));
        }

        Converter<S, T> converter = null;
        if (targetClass.isAssignableFrom(sourceClass)) {
            converter = new NullConverter<S, T>();
        } else {
            Key key = new Key(sourceClass, targetClass);
            if (directConverterMap.containsKey(key)) {
                // 如果有直接转换关系，则使用之。
                converter = directConverterMap.get(key);
            } else {
                BaseConverter sourceConv = getBaseConverter(sourceClass);
                if (sourceConv == null) {
                    throw new ConvertException(String.format(
                            "源类型[%s]未注册类型转换器。", sourceClass.getName()));
                }
                BaseConverter targetConv = getBaseConverter(targetClass);
                if (targetConv == null) {
                    throw new ConvertException(String.format(
                            "目标类型[%s]未注册类型转换器。", targetClass.getName()));
                }
                converter = new DefaultConverter<S, T>(sourceConv, targetConv);
            }
        }

        return converter;
    }

    public boolean hasConvertor(Class sourceClass, Class targetClass) {
        if (sourceClass == null || targetClass == null) {
            return false;
        }
        Key key = new Key(sourceClass, targetClass);
        if (directConverterMap.containsKey(key)) {
            return true;
        } else {
            BaseConverter sourceConv = getBaseConverter(sourceClass);
            if (sourceConv == null) {
                return false;
            }
            BaseConverter targetConv = getBaseConverter(targetClass);
            return targetConv != null;
        }
    }

    /**
     * 通过类型返回其相应的转换器，如果原始类型未找到，则使用继承机制进行查找。
     *
     * @param clazz
     * @return
     */
    protected BaseConverter getBaseConverter(Class clazz) {
        if (clazz == null) {
            return null;
        }
        BaseConverter conv = commonConverterMap.get(clazz);
        if (conv == null) {
            for (Iterator<Class> it = commonConverterMap.keySet().iterator(); it.hasNext();) {
                Class convClass = it.next();
                if (convClass.isAssignableFrom(clazz)) {
                    conv = commonConverterMap.get(convClass);
                }
            }
        }
        return conv;
    }

    static private class Key<S, T> {

        private final Class<S> source;
        private final Class<T> target;

        public Key(Class<S> source, Class<T> target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public int hashCode() {
            // 在某些情况下，转换注册的是其基类，比较的是其子类，所以，不能以HashCode进行区分。
            int result = 3;
//            result += (source == null ? 0 : source.hashCode());
//            result += (target == null ? 0 : target.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            if (other.source == null) {
                if (source != null) {
                    return false;
                }
            } else if (!other.source.isAssignableFrom(source)) {
                return false;
            }
            if (other.target == null) {
                if (target != null) {
                    return false;
                }
            } else if (!other.target.isAssignableFrom(target)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder strBld = new StringBuilder();
            strBld.append(super.toString()).append('[');
            strBld.append("source=").append(source).append(';');
            strBld.append("target=").append(target);
            strBld.append(']');
            return strBld.toString();
        }
    }
    /**
     * 系统默认的转换器
     */
    static final BaseConverter<String> STRING_BASE_CONVERTER = new BaseConverter<String>() {

        public String convert(String value) {
            return value;
        }

        public String convertToString(String obj) {
            return obj;
        }

        public Class<String> getConvertedClass() {
            return String.class;
        }
    };
    static final BaseConverter<Byte> BYTE_BASE_CONVERTER = new BaseConverter<Byte>() {

        public Byte convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return value.getBytes()[0];
        }

        public String convertToString(Byte obj) {
            if (obj == null) {
                return null;
            }
            return Byte.toString(obj);
        }

        public Class<Byte> getConvertedClass() {
            return Byte.class;
        }
    };
    static final BaseConverter<Short> SHORT_BASE_CONVERTER = new BaseConverter<Short>() {

        public Short convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            // 对来自double、float、int 和long类型的数据进行自动降级。
            Double d = Double.valueOf(value);
            return d.shortValue();
        }

        public String convertToString(Short obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Short> getConvertedClass() {
            return Short.class;
        }
    };
    static final BaseConverter<Integer> INT_BASE_CONVERTER = new BaseConverter<Integer>() {

        public Integer convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            // 对来自double、float 和long类型的数据进行自动降级。
            Double d = Double.valueOf(value);
            return d.intValue();
        }

        public String convertToString(Integer obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Integer> getConvertedClass() {
            return Integer.class;
        }
    };
    static final BaseConverter<Long> LONG_BASE_CONVERTER = new BaseConverter<Long>() {

        public Long convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            // 对来自double、float 和long类型的数据进行自动降级。
            Double d = Double.valueOf(value);
            return d.longValue();
        }

        public String convertToString(Long obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Long> getConvertedClass() {
            return Long.class;
        }
    };
    static final BaseConverter<Float> FLOAT_BASE_CONVERTER = new BaseConverter<Float>() {

        public Float convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            // 对来自double、float 和long类型的数据进行自动降级。
            Double d = Double.valueOf(value);
            return d.floatValue();
        }

        public String convertToString(Float obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Float> getConvertedClass() {
            return Float.class;
        }
    };
    static final BaseConverter<Double> DOUBLE_BASE_CONVERTER = new BaseConverter<Double>() {

        public Double convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return Double.valueOf(value);
        }

        public String convertToString(Double obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Double> getConvertedClass() {
            return Double.class;
        }
    };
    static final BaseConverter<Character> CHAR_BASE_CONVERTER = new BaseConverter<Character>() {

        public Character convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return value.toCharArray()[0];
        }

        public String convertToString(Character obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Character> getConvertedClass() {
            return Character.class;
        }
    };
    static final BaseConverter<Boolean> BOOLEAN_BASE_CONVERTER = new BaseConverter<Boolean>() {

        public Boolean convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return BooleanUtils.isTrue(value);
        }

        public String convertToString(Boolean obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Boolean> getConvertedClass() {
            return Boolean.class;
        }
    };
    /**
     * 在类及其名称之间进行转换
     */
    static final BaseConverter<Class> CLASS_BASE_CONVERTER = new BaseConverter<Class>() {

        public Class convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            try {
                return Class.forName(value);
            } catch (ClassNotFoundException ex) {
                throw new ConvertException(String.format("未发现类[%s]。", value), ex);
            }
        }

        public String convertToString(Class obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<Class> getConvertedClass() {
            return Class.class;
        }
    };
    static final BaseConverter<BigInteger> BIGINTEGER_BASE_CONVERTER = new BaseConverter<BigInteger>() {

        public BigInteger convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return new BigInteger(value);
        }

        public String convertToString(BigInteger obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<BigInteger> getConvertedClass() {
            return BigInteger.class;
        }
    };
    static final BaseConverter<BigDecimal> BIGDECIMAL_BASE_CONVERTER = new BaseConverter<BigDecimal>() {

        public BigDecimal convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return new BigDecimal(value);
        }

        public String convertToString(BigDecimal obj) {
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }

        public Class<BigDecimal> getConvertedClass() {
            return BigDecimal.class;
        }
    };
    static final BaseConverter<java.sql.Time> SQL_TIME_BASE_CONVERTER = new BaseConverter<java.sql.Time>() {

        public java.sql.Time convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            long time = Long.parseLong(value);
            return new java.sql.Time(time);
        }

        public String convertToString(java.sql.Time obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime());
        }

        public Class<java.sql.Time> getConvertedClass() {
            return java.sql.Time.class;
        }
    };
    static final BaseConverter<java.sql.Timestamp> SQL_TIMESTAMP_BASE_CONVERTER = new BaseConverter<java.sql.Timestamp>() {

        public java.sql.Timestamp convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            long time = Long.parseLong(value);
            return new java.sql.Timestamp(time);
        }

        public String convertToString(java.sql.Timestamp obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime());
        }

        public Class<java.sql.Timestamp> getConvertedClass() {
            return java.sql.Timestamp.class;
        }
    };
    static final BaseConverter<java.sql.Date> SQL_DATE_BASE_CONVERTER = new BaseConverter<java.sql.Date>() {

        public java.sql.Date convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            long time = Long.parseLong(value);
            return new java.sql.Date(time);
        }

        public String convertToString(java.sql.Date obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime());
        }

        public Class<java.sql.Date> getConvertedClass() {
            return java.sql.Date.class;
        }
    };
    static final BaseConverter<java.util.Date> UTIL_DATE_BASE_CONVERTER = new BaseConverter<java.util.Date>() {

        public java.util.Date convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            long time = Long.parseLong(value);
            return new java.util.Date(time);
        }

        public String convertToString(java.util.Date obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime());
        }

        public Class<java.util.Date> getConvertedClass() {
            return java.util.Date.class;
        }
    };

    static private boolean isLong(String longStr) {
        if (StringUtils.isEmpty(longStr)) {
            return false;
        }
        
        return longStr.matches(DEFAULT_LONG_PATTERN);
    }
    
    static final BaseConverter<Calendar> CALENDAR_BASE_CONVERTER = new BaseConverter<Calendar>() {

        public Calendar convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            if (isLong(value)) {
                long time = Long.parseLong(value);
                Calendar cal = Calendar.getInstance();
                java.util.Date date = new java.util.Date(time);
                cal.setTime(date);
                return cal;
            } else {
                return DateUtils.parseCalendar(value);
            }
        }

        public String convertToString(Calendar obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime().getTime());
        }

        public Class<Calendar> getConvertedClass() {
            return Calendar.class;
        }
    };
    static final BaseConverter<GregorianCalendar> GREGORIAN_CALENDAR_BASE_CONVERTER = new BaseConverter<GregorianCalendar>() {

        public GregorianCalendar convert(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            long time = Long.parseLong(value);
            GregorianCalendar cal = new GregorianCalendar();
            java.util.Date date = new java.util.Date(time);
            cal.setTime(date);
            return cal;
        }

        public String convertToString(GregorianCalendar obj) {
            if (obj == null) {
                return null;
            }
            return Long.toString(obj.getTime().getTime());
        }

        public Class<GregorianCalendar> getConvertedClass() {
            return GregorianCalendar.class;
        }
    };

    static {
        instance.register(STRING_BASE_CONVERTER);

        // 基本类型
        instance.register(BYTE_BASE_CONVERTER);
        instance.register(SHORT_BASE_CONVERTER);
        instance.register(INT_BASE_CONVERTER);
        instance.register(LONG_BASE_CONVERTER);
        instance.register(FLOAT_BASE_CONVERTER);
        instance.register(DOUBLE_BASE_CONVERTER);
        instance.register(BOOLEAN_BASE_CONVERTER);
        instance.register(CHAR_BASE_CONVERTER);
        instance.register(BIGINTEGER_BASE_CONVERTER);
        instance.register(BIGDECIMAL_BASE_CONVERTER);

        // 日期类型
        instance.register(GREGORIAN_CALENDAR_BASE_CONVERTER);
        instance.register(CALENDAR_BASE_CONVERTER);
        instance.register(UTIL_DATE_BASE_CONVERTER);
        instance.register(SQL_DATE_BASE_CONVERTER);
        instance.register(SQL_TIME_BASE_CONVERTER);
        instance.register(SQL_TIMESTAMP_BASE_CONVERTER);

        // “类”类型
        instance.register(CLASS_BASE_CONVERTER);

        // 注册 Blob 和 Clob 相关的类型
        instance.register(new ByteArrayConverter());
        instance.register(new InputStreamConverter());
        instance.register(new BlobConverter());
        instance.register(new CharsConverter());
        instance.register(new ClobConverter());
        instance.register(new ReaderConverter());
    }
    // //////////////////////////////////////////////////////////////////////////
}

// class Ba
