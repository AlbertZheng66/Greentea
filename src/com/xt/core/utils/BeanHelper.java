package com.xt.core.utils;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.cp.PropertyReader;
import com.xt.core.utils.cp.PropertyWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;

/**
 * <p>
 * Title: 框架类.
 * </p>
 * <p>
 * Description: 对象操作工具类。拥有取得对象的相关属性，复制对象等方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */
abstract public class BeanHelper {

    private static final Logger logger = Logger.getLogger(BeanHelper.class);
    
    static {
        // 这里一定要注册默认值，使用null也可以    
        BigDecimalConverter bd = new BigDecimalConverter(null);
        
        BigIntegerConverter bi = new BigIntegerConverter(null);
        ConvertUtils.register(bd, java.math.BigDecimal.class);
        ConvertUtils.register(bi, java.math.BigInteger.class);
    }

    public static boolean hasProperty(Object obj, String propertyName) {
        if (obj == null || propertyName == null) {
            return false;
        }
        try {
            Map map = PropertyUtils.describe(obj);
            return map.containsKey(propertyName);
        } catch (Exception e) {
            // ignore error;
            e.printStackTrace();
        }
        return false;
    }

    public static void copy(Object dest, Object source) {
        try {
            // if (dest != null && dest != null && dest.getClass() !=
            // source.getClass()) {
            // throw new BadParameterException("赋值的必须拥有相同的类型！");
            // }
            if (dest instanceof PropertyWriter) {
                // 采用字节码方式复制属性
                PropertyWriter pw = (PropertyWriter) dest;
                pw.duplicate(source);
            } else {
                // 采用反射方式复制属性
                BeanUtils.copyProperties(dest, source);
            }
        } catch (IllegalAccessException e) {
            throw new SystemException("对象复制时发生非法权限异常！", e);
        } catch (InvocationTargetException e) {
            throw new SystemException("对象复制时发生调用目标异常！", e);
        }
    }

    public static void copyProperty(Object bean, String name, Object value) {
        try {
            if (bean == null) {
                throw new SystemException("被赋值的对象不能为空！");
            }
            if (name == null || name.trim().length() == 0) {
                throw new SystemException("名称不能为空！");
            }
            // 只要名称里含有了如下符号".\[,",就认为使用了OGNL语言,
            if (name.matches(".*[\\.\\[\\(\\{\\,].*")) {
                if (name.indexOf(".") > -1) {
                    // 初始化第一个属性
                    String propertyName = (name.split("\\."))[0];
                    Object obj = getProperty(bean, propertyName);
                    if (obj == null) {
                        Class clazz = ClassHelper.getFieldType(bean.getClass(),
                                propertyName);
                        // 属性类型返回空，表示此属性不存在
                        if (clazz != null) {
                            if (clazz.isArray()) {
                                throw new SystemException("目前还不支持数组类型");
                            } else {
                                // 当类型不同时自动进行类型转换
                                Class targetClass = ClassHelper.getFieldType(bean.getClass(),
                                        name);
                                // 当类型不同时自动进行类型转换
                                if (value != null && targetClass != value.getClass() /*!targetClass.isAssignableFrom(value.getClass())*/) {
                                    Class sourceClass = value.getClass();
                                    Converter converter = ConverterFactory.getInstance().getConverter(sourceClass, targetClass);
                                    value = converter.convert(sourceClass, targetClass, value);
                                }
                                BeanHelper.copyProperty(bean, propertyName,
                                        clazz.newInstance());
                            }
                        }
                    }
                }
                Ognl.setValue(name, bean, value);
            } else {
                if (bean instanceof PropertyWriter) {
                    // 采用字节码方式复制属性
                    PropertyWriter pw = (PropertyWriter) bean;
                    pw.setProperty(name, value);
                } else {
                    Class targetClass = ClassHelper.getFieldType(bean.getClass(),
                            name);
                                // 当类型不同时自动进行类型转换
                    if (value != null && targetClass != value.getClass() /*!targetClass.isAssignableFrom(value.getClass())*/) {
                        Class sourceClass = value.getClass();
                        Converter converter = ConverterFactory.getInstance().getConverter(sourceClass, targetClass);
                        value = converter.convert(sourceClass, targetClass, value);
                    }
                    // 采用反射方式复制属性
                    // logger.debug("bean=" + bean + "; name=" + name +  "; value=" + value);
                    BeanUtils.copyProperty(bean, name, value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new SystemException(String.format("对象[%s]复制属性[%s]时发生非法权限异常！",
                    bean.toString(), name), e);
        } catch (InvocationTargetException e) {
            throw new SystemException(String.format("对象[%s]复制属性[%s]时发生调用目标异常！",
                    bean.toString(), name), e);
        } catch (OgnlException e) {
            throw new SystemException(String.format(
                    "对象[%s]复制属性[%s]时发生OGNL脚本异常！", bean.toString(), name), e);
        } catch (InstantiationException e) {
            throw new SystemException(String.format("对象[%s]复制属性[%s]时发生实例化异常！",
                    bean.toString(), name), e);
        }
    }

    /**
     * 复制一个数组属性
     *
     * @param bean
     * @param name
     * @param arrayValue
     */
    public static void copyProperty(Object bean, String name,
            Object[] arrayValue) {
        try {
            if (bean == null) {
                throw new SystemException("被赋值的对象不能为空！");
            }
            if (name == null || name.trim().length() == 0) {
                throw new SystemException("名称不能为空！");
            }

            if (arrayValue == null) {
                return;
            }
            // 只要名称里含有了如下符号".\[,",就认为使用了OGNL语言,
            if (name.matches(".*[\\.\\[\\(\\{\\,].*")) {
                throw new SystemException("属性名称[" + name + "]非法");
            } else {
                Class clazz = ClassHelper.getFieldType(bean.getClass(), name);
                Class propertyClass = ClassHelper.getClassFromArray(clazz);
                LogWriter.debug("propertyClass", propertyClass);
                Object[] objs = (Object[]) Array.newInstance(propertyClass,
                        arrayValue.length);
                LogWriter.debug("objs", objs.getClass());
                for (int i = 0; i < objs.length; i++) {
                    Array.set(objs, i, arrayValue[i]);
                }
                BeanUtils.copyProperty(bean, name, objs);

            }
        } catch (IllegalAccessException e) {
            throw new SystemException("对象复制时发生非法权限异常！", e);
        } catch (InvocationTargetException e) {
            throw new SystemException("对象复制时发生调用目标异常！", e);
        }
    }

    /**
     * 根据传入的Class实例化一个对象，并将传入的属性赋给已经实例化的Bean
     *
     * @param clazz
     *            Class 被实例化的类
     * @param nameValuePair
     *            Pair[] 属性名称与其对应的属性值组成的名值对
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return Object 实例化的对象
     */
    public static Object copyProperty(Class clazz, Pair[] nameValuePair) {
        // 实例化Class
        Object result = null;
        try {
            result = ClassHelper.newInstance(clazz);

            // 逐个赋予属性
            if (nameValuePair != null) {
                for (int i = 0; i < nameValuePair.length; i++) {
                    String name = nameValuePair[i].getName();
                    Object value = nameValuePair[i].getValue();

                    if (result instanceof PropertyWriter) {
                        // 采用字节码方式复制属性
                        PropertyWriter pw = (PropertyWriter) result;
                        pw.setProperty(name, value);
                    } else {
                        // 如果属性存在
                        if (ClassHelper.getField(clazz, name) != null) {
                            // 采用反射方式复制属性
                            BeanUtils.copyProperty(result, name, value);
                        }
                    }
                }
            }
        } catch (InvocationTargetException ex) {
            // 不处理
            LogWriter.warn(logger, "复制属性是产生异常", ex);
        } catch (IllegalAccessException ex) {
            // 不处理
            LogWriter.warn(logger, "复制属性是产生异常", ex);
        }

        return result;
    }

    /**
     * 根据传入的Class实例化一个对象，并将传入的属性赋给已经实例化的Bean
     *
     * @param clazz
     *            Class 被实例化的类
     * @param nameValuePair
     *            Pair[] 属性名称与其对应的属性值组成的名值对
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return Object 实例化的对象
     */
    public static Pair<Object>[] getProperties(Object bean) {
        Pair<Object>[] result = null;
        String[] pns = ClassHelper.getPropertyNames(bean.getClass());
        if (null != pns) {
            result = new Pair[pns.length];
            for (int i = 0; i < pns.length; i++) {
                Pair<Object> p = new Pair<Object>();
                p.setName(pns[i]);
                p.setValue(getProperty(bean, pns[i]));
                result[i] = p;
            }
        }
        return result;
    }

    public static Object getProperty(Object bean, String propertyName) {
        Object result = null;
        try {
            if (bean instanceof PropertyReader) {
                // 采用字节码方式复制属性
                PropertyReader pr = (PropertyReader) bean;
                result = pr.getPropertyValue(propertyName);
            } else {
                // 采用反射方式复制属性
                result = PropertyUtils.getProperty(bean, propertyName);
            }
            return result;
        } catch (NoSuchMethodException ex) {
            LogWriter.warn(logger, String.format(
                    "查找Bean[%s]的属性[%s]时发生 NoSuchMethodException。", bean.toString(), propertyName), ex);
        } catch (InvocationTargetException ex) {
            LogWriter.warn(logger, String.format(
                    "查找Bean[%s]的属性[%s]时发生 InvocationTargetException。", bean.toString(), propertyName), ex);
        } catch (IllegalAccessException ex) {
            LogWriter.warn(logger, String.format(
                    "查找Bean[%s]的属性[%s]时发生 IllegalAccessException。", bean.toString(), propertyName), ex);
        }
        return result;
    }

    static public <T> T clone(T row) {
        if (row == null) {
            return null;
        }
        T newRow = null;
        if (row instanceof Cloneable) {
            try {
                // 使用原生的克隆方式
                Method method = row.getClass().getMethod("clone", new Class[0]);
                if (method.isAccessible()) {
                    newRow = (T) method.invoke(row, new Object[0]);
                }
            } catch (Exception ex) {
                throw new SystemException(String.format("克隆对象[%s]时参数异常。", row.toString()), ex);
            }
        } else {
            newRow = (T) ClassHelper.newInstance(row.getClass());
            // 此处可能存在着浅拷贝，为了避免这个问题，需要使用上面的克隆方式进行解决
            BeanHelper.copy(newRow, row);
        }
        return newRow;
    }
    
    /**
     * 读取非标准JavaBean的属性值。首先读取当前Bean所有的域，然后使用约定的Getter方法读取值。
     * @param obj
     * @return 
     */
    static public Map<String, Object> getFieldValues(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> values = new HashMap();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Method getter = ClassHelper.getGetterMethod(obj.getClass(), field.getName());
            if (getter != null && getter.getReturnType().equals(field.getType())) {
                try {
                    Object value = getter.invoke(obj, new Object[0]);
                    if (value != null) {
                        values.put(field.getName(), value);
                    }
                } catch (Exception ex) {
                    LogWriter.warn2(logger, ex,  "读取对象[%s]的属性[%s]是出现异常。",
                            obj.getClass(), field.getName());
                }                
            }
        }
        return values;
    }
    
}
