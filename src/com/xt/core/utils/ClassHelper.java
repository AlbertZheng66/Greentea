package com.xt.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import com.xt.core.exception.SystemException;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * <p>Title: XT框架-工具-类辅助类</p>
 * <p>Description: 类的帮助类.这些方法来自Apache的OJB项目,提供了一些方便操作类的方法.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */
/**
 * Helper class with static methods for java class, method, and field handling.
 * 
 * @version $Id: ClassHelper.java,v 1.2 2006/09/17 00:46:35 zhengwei Exp $
 */
public class ClassHelper {

    /** Arguments for invoking a default or no-arg constructor */
    private static final Object[] NO_ARGS = {};
    /** Parameter types of a default/no-arg constructor */
    private static final Class[] NO_ARGS_CLASS = {};
    /** The class loader currently used by OJB */
    private static ClassLoader _classLoader = null;
    /** A mutex for changing the class loader */
    private static Object _mutex = new Object();
    @SuppressWarnings("unchecked")
    private static Map<Class, Class> primitiveMap = new HashMap<Class, Class>();
    @SuppressWarnings("unchecked")
    private static Map<Class, Class> wrapperMap = new HashMap<Class, Class>();
    @SuppressWarnings("unchecked")
    private static Map<Class, Object> primitiveInitialValueMap = new HashMap<Class, Object>();

    static {
        wrapperMap.put(int.class, Integer.class);
        wrapperMap.put(double.class, Double.class);
        wrapperMap.put(long.class, Long.class);
        wrapperMap.put(float.class, Float.class);
        wrapperMap.put(char.class, Character.class);
        wrapperMap.put(byte.class, Byte.class);
        wrapperMap.put(boolean.class, Boolean.class);
        wrapperMap.put(short.class, Short.class);

        primitiveMap.put(Integer.class, int.class);
        primitiveMap.put(Double.class, double.class);
        primitiveMap.put(Long.class, long.class);
        primitiveMap.put(Float.class, float.class);
        primitiveMap.put(Character.class, char.class);
        primitiveMap.put(Byte.class, byte.class);
        primitiveMap.put(Boolean.class, boolean.class);
        primitiveMap.put(Short.class, short.class);


        primitiveInitialValueMap.put(int.class, new Integer(0));
        primitiveInitialValueMap.put(double.class, new Double(0));
        primitiveInitialValueMap.put(long.class, new Long(0));
        primitiveInitialValueMap.put(float.class, new Float(0));
        primitiveInitialValueMap.put(char.class, new Character('0'));
        primitiveInitialValueMap.put(byte.class, new Byte((byte) 0));
        primitiveInitialValueMap.put(boolean.class, Boolean.FALSE);
        primitiveInitialValueMap.put(short.class, new Short((short) 0));
    }

    /**
     * Prevents instatiation.
     */
    private ClassHelper() {
    }

    /**
     * 返回指定类所在的Jar文件。
     * @param clazz
     * @return Jar 文件的 URL 路径，如果类文件为空，则返回空。
     */
    public static URL getLocation(Class clazz) {
        if (clazz == null) {
            return null;
        }
        CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        return cs.getLocation();
    }

    /**
     * 返回一个类型的包装类，如果参数为空，则返回空。如果参数类型非原始类型，
     * 则返回参数，否则，返回其对应的包装类。
     * @param clazz
     * @return
     */
    public static Class getWrapper(Class clazz) {
        if (clazz == null || !clazz.isPrimitive()) {
            return clazz;
        }
        return wrapperMap.get(clazz);
    }

    /**
     * 返回一个包装类型的原始类，如果参数为空，则返回空。如果参数类型非类型类型，
     * 则返回参数，否则，返回其对应的原始类。
     * @param clazz
     * @return
     */
    public static Class getPrimitive(Class clazz) {
        if (clazz == null || !primitiveMap.containsKey(clazz)) {
            return clazz;
        }
        return primitiveMap.get(clazz);
    }

    /**
     * 返回原始类型的初始值，传入的类型为空，或者非原始类型，则抛出系统异常。
     * @param clazz
     * @return
     */
    public static Object getPrimitiveInitialValue(Class clazz) throws SystemException {
        if (clazz == null || !clazz.isPrimitive()) {
            throw new SystemException("类型不能为空，请必须是原始类型。");
        }
        return primitiveInitialValueMap.get(clazz);
    }

    /**
     * Sets the classloader to be used by OJB. This can be set by external
     * application that need to pass a specific classloader to OJB.
     * 
     * @param loader
     *            The class loader. If <code>null</code> then OJB will use the
     *            class loader of the current thread
     */
    public static void setClassLoader(ClassLoader loader) {
        synchronized (_mutex) {
            _classLoader = loader;
        }
    }

//	/**
//	 * 确定一个类是否实现了某个接口
//	 * 
//	 * @param clazz
//	 *            类
//	 * @param interClass
//	 *            接口
//	 * @return
//	 */
//	public static boolean isImplements(Class clazz, Class interClass) {
//		if (clazz != null && interClass != null) {
//			Class[] inters = clazz.getInterfaces();
//			for (int i = 0; i < inters.length; i++) {
//				if (interClass == inters[i]) {
//					return true;
//				}
//			}
//
//			// 如果本级的类没有实现此接口，查看父类是否实现
//			Class superClass = clazz.getSuperclass();
//			if (superClass != null) {
//				return isImplements(superClass, interClass);
//			}
//		}
//		return false;
//	}
    /**
     * Returns the class loader currently used by OJB. Defaults to the class
     * loader of the current thread (
     * <code>Thread.currentThread().getContextClassLoader()</code>) if not set
     * differently.
     * 
     * @return The classloader used by OJB
     */
    public static ClassLoader getClassLoader() {
        return _classLoader == null ? Thread.currentThread().getContextClassLoader() : _classLoader;
    }

    /**
     * 返回指定类的去除包名称以后的类名称
     */
    public static String getShortName(Class clazz) {
        String name = clazz.getName();
        if (clazz.isArray()) {
            name = getClassFromArray(clazz).getName();
        }

        // 最后一个类的分割符号"."的位置
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    /**
     * Determines the url of the indicated resource using the currently set
     * class loader.
     * 
     * @param name
     *            The resource name
     * @return The resource's url
     */
    public static URL getResource(String name) {
        return getClassLoader().getResource(name);
    }

    /**
     * Retrieves the class object for the given qualified class name.
     * 
     * @param className
     *            The qualified name of the class
     * @param initialize
     *            Whether the class shall be initialized
     * @return The class object
     */
    public static Class getClass(String className, boolean initialize)
            throws ClassNotFoundException {
        return Class.forName(className, initialize, getClassLoader());
    }

    /**
     * 返回数组类的生成类. If this class object represents a class of arrays, then the
     * internal form of the name consists of the name of the element type
     * preceded by one or more '[' characters representing the depth of the
     * array nesting. The encoding of element type names is as follows: Element
     * Type Encoding boolean Z byte B char C class or interface Lclassname;
     * double D float F int I long J short S
     */
    public static Class getClassFromArray(Class clazz) {
        Class ret = clazz;
        if (clazz != null && clazz.isArray()) {
            String pattern = "\\[+[LZBCDFIJS]"; // 数组表示法的匹配模式
            String arrayClassName = clazz.getName();
            Matcher m = RegExpUtils.generate(pattern, arrayClassName);
            if (m.find()) {
                char endChar = arrayClassName.charAt(m.end() - 1); // 数组表示法的结尾字符
                switch (endChar) {
                    case 'L':
                        // 去掉前面的数组符号
                        String temp = arrayClassName.substring(m.end());
                        // 去掉后面的分号
                        String className = temp.substring(0, temp.length() - 1);
                        ret = getClass(className);
                        break;
                    case 'Z':
                        ret = boolean.class;
                        break;
                    case 'B':
                        ret = byte.class;
                        break;
                    case 'C':
                        ret = char.class;
                        break;
                    case 'D':
                        ret = double.class;
                        break;
                    case 'F':
                        ret = float.class;
                        break;
                    case 'I':
                        ret = int.class;
                        break;
                    case 'J':
                        ret = long.class;
                        break;
                    case 'S':
                        ret = short.class;
                        break;
                }
            }
        }

        return ret;
    }

    /**
     * Returns a new instance of the given class, using the default or a no-arg
     * constructor.
     * 
     * @param target
     *            The class to instantiate
     * @return The instance
     */
    public static <T> T newInstance(Class<T> target) {
        try {
            return target.newInstance();
        } catch (InstantiationException e) {
            throw new SystemException("实例化类[" + target + "]异常!", e);
        } catch (IllegalAccessException e) {
            throw new SystemException("非法存取类[" + target + "]异常!", e);
        }
    }

    /**
     * Returns a new instance of the given class, using the default or a no-arg
     * constructor. This method can also use private no-arg constructors if
     * <code>makeAccessible</code> is set to <code>true</code> (and there are no
     * other security constraints).
     * 
     * @param target
     *            The class to instantiate
     * @param makeAccessible
     *            If the constructor shall be made accessible prior to using it
     * @return The instance
     */
    public static Object newInstance(Class target, boolean makeAccessible)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (makeAccessible) {
            return newInstance(target, NO_ARGS_CLASS, NO_ARGS, makeAccessible);
        } else {
            return target.newInstance();
        }
    }

    /**
     * Returns a new instance of the given class, using the constructor with the
     * specified parameter types.
     * 
     * @param target
     *            The class to instantiate
     * @param types
     *            The parameter types
     * @param args
     *            The arguments
     * @return The instance
     */
    public static Object newInstance(Class target, Class[] types, Object[] args)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        return newInstance(target, types, args, false);
    }

    /**
     * Returns a new instance of the given class, using the constructor with the
     * specified parameter types. This method can also use private constructors
     * if <code>makeAccessible</code> is set to <code>true</code> (and there are
     * no other security constraints).
     * 
     * @param target
     *            The class to instantiate
     * @param types
     *            The parameter types
     * @param args
     *            The arguments
     * @param makeAccessible
     *            If the constructor shall be made accessible prior to using it
     * @return The instance
     */
    public static Object newInstance(Class target, Class[] types,
            Object[] args, boolean makeAccessible)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Constructor con;

        if (makeAccessible) {
            con = target.getDeclaredConstructor(types);
            if (makeAccessible && !con.isAccessible()) {
                con.setAccessible(true);
            }
        } else {
            con = target.getConstructor(types);
        }
        return con.newInstance(args);
    }

    /**
     * Returns a new instance of the given class, using the private constructor
     * without the parameter types. This method can also use private
     * constructors if <code>makeAccessible</code> is set to <code>true</code>
     * (and there are no other security constraints).
     * 
     * @param className
     *            The class to instantiate
     * @return The instance
     */
    public static Object newPrivateInstance(String className)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException {
        Object obj = null;
        Class target = getClass(className);
        // 得到所有的构造函数
        Constructor[] cons = target.getDeclaredConstructors();
        for (int i = 0; i < cons.length; i++) {
            Constructor con = cons[i];
            // 读取构造函数的参数等于0的那个构造参数
            if (con.getParameterTypes().length == 0) {
                con.getParameterTypes();
                con.setAccessible(true);
                obj = con.newInstance();
                break;
            }
        }

        return obj;
    }

    /**
     * Determines the method with the specified signature via reflection
     * look-up.
     * 
     * @param clazz
     *            The java class to search in
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return The method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getMethod(Class clazz, String methodName,
            Class[] params) {
        try {
            Method method = clazz.getMethod(methodName, params);
            if (null == method) {
            }
            return method;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 返回指定属性的Getter方法.
     * 
     * @param clazz
     *            The java class to search in
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return The method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getGetterMethod(Class clazz, String fieldName) {
        // Getter方法的名称
        String getter = "get" + StringUtils.uppercaseCapital(fieldName);
        // FIXME: 如果是Boolean类型的属性，可能使用“is”约定
        try {
            Method method = clazz.getMethod(getter, new Class[0]);
            return method;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 返回指定属性的Setter方法.
     * 
     * @param clazz
     *            The java class to search in
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return The method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getSetterMethod(Class clazz, String fieldName) {
        try {
            Method getter = getGetterMethod(clazz, fieldName);
            // Setter方法的名称
            String setterName = "set" + StringUtils.uppercaseCapital(fieldName);
            return clazz.getMethod(setterName, new Class[]{getter.getReturnType()});
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Determines the field via reflection look-up.
     * 
     * @param clazz
     *            The java class to search in
     * @param fieldName
     *            The field's name
     * @return The field object or <code>null</code> if no matching field was
     *         found
     */
    public static Field getField(Class clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (Exception ignored) {
        }
        return field;
    }

    /**
     * Determines the field via reflection look-up.
     * 递推的查找其父类属性（如果此类不包含此属性，则采用相应的方法查找其父类是否存在。）。
     * 
     * @param clazz
     *            The java class to search in
     * @param fieldName
     *            The field's name
     * @return The field object or <code>null</code> if no matching field was
     *         found
     */
    public static Field getField(Class clazz, String fieldName,
            boolean recursive) {
        Field field = null;
        field = getField(clazz, fieldName);

        if (recursive) {
            Class targetClass = clazz;
            while (field == null && targetClass != null
                    && targetClass != Object.class) {
                targetClass = targetClass.getSuperclass();
                field = getField(targetClass, fieldName);

            }
        }
        return field;
    }
    
    public static List<Field> getFields(Class clazz, boolean recursive) {
        Set<Field> fields = new HashSet();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        if (recursive) {
            Class targetClass = clazz;
            while (targetClass != null
                    && targetClass != Object.class) {
                targetClass = targetClass.getSuperclass();
                fields.addAll(Arrays.asList(targetClass.getDeclaredFields()));
            }
        }
        return fields;
    }

    // *******************************************************************
    // Convenience methods
    // *******************************************************************
    /**
     * Convenience method for {@link #getClass(String, boolean) getClass(name,
     * true)}
     * 
     * @param name
     *            The qualified class name
     * @return The class object
     */
    public static Class getClass(String name) {
        try {
            return getClass(name, true);
        } catch (ClassNotFoundException e) {
            throw new SystemException("系统未发现类[" + name + "]定义！", e);
        }
    }

    /**
     * Returns a new instance of the class with the given qualified name using
     * the default or or a no-arg constructor.
     * 
     * @param className
     *            The qualified name of the class to instantiate
     */
    public static Object newInstance(String className) {
        return newInstance(getClass(className));
    }

    /**
     * Returns a new instance of the class with the given qualified name using
     * the constructor with the specified signature.
     * 
     * @param className
     *            The qualified name of the class to instantiate
     * @param types
     *            The parameter types
     * @param args
     *            The arguments
     * @return The instance
     */
    public static Object newInstance(String className, Class[] types,
            Object[] args) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException, ClassNotFoundException {
        return newInstance(getClass(className), types, args);
    }

    /**
     * Returns a new instance of the given class using the constructor with the
     * specified parameter.
     * 
     * @param target
     *            The class to instantiate
     * @param type
     *            The types of the single parameter of the constructor
     * @param arg
     *            The argument
     * @return The instance
     */
    public static Object newInstance(Class target, Class type, Object arg)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        return newInstance(target, new Class[]{type}, new Object[]{arg});
    }

    /**
     * Returns a new instance of the class with the given qualified name using
     * the constructor with the specified parameter.
     * 
     * @param className
     *            The qualified name of the class to instantiate
     * @param type
     *            The types of the single parameter of the constructor
     * @param arg
     *            The argument
     * @return The instance
     */
    public static Object newInstance(String className, Class type, Object arg)
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        return newInstance(className, new Class[]{type},
                new Object[]{arg});
    }

    /**
     * Determines the method with the specified signature via reflection
     * look-up.
     * 
     * @param object
     *            The instance whose class is searched for the method
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return A method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getMethod(Object object, String methodName,
            Class[] params) {
        return getMethod(object.getClass(), methodName, params);
    }

    /**
     * Determines the method with the specified signature via reflection
     * look-up.
     * 
     * @param className
     *            The qualified name of the searched class
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return A method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getMethod(String className, String methodName,
            Class[] params) {
        try {
            return getMethod(getClass(className, false), methodName, params);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 由程序员保证方法的名称不被重载，方法只是找到一个同名函数即返回。
     * 
     * @param className
     *            The qualified name of the searched class
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return A method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getMethod(String className, String methodName) {
        try {
            return getMethod(getClass(className, false), methodName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 由程序员保证方法的名称不被重载，方法只是找到一个同名函数即返回。
     * 
     * @param className
     *            The qualified name of the searched class
     * @param methodName
     *            The method's name
     * @param params
     *            The parameter types
     * @return A method object or <code>null</code> if no matching method was
     *         found
     */
    public static Method getMethod(Class clazz, String methodName) {
        try {
            Method[] ms = clazz.getMethods();
            for (int i = 0; i < ms.length; i++) {
                if (ms[i].getName().equals(methodName)) {
                    return ms[i];
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 返回一个类中的所有的属性名称.判别的方法是检查一个类的所有方法, 找到成对的 setter和getter方法,并根据这两个方法提取属性名称.
     * 
     * @param clazz
     *            Class 被检查的类
     * @return String[] 所有的属性名称
     */
    public static String[] getPropertyNames(Class clazz) {
        Method[] methods = clazz.getMethods();
        ArrayList fieldNames = new ArrayList(methods.length / 2);
        for (int j = 0; j < methods.length; j++) {
            Method method = methods[j];
            if (isGetter(method) && hasSetter(methods, method)) {
                String methodName = method.getName();
                if (methodName.length() > 3) {
                    fieldNames.add(StringUtils.lowcaseCapital(methodName.substring(3)));
                }
            }
        }

        return (String[]) fieldNames.toArray(new String[fieldNames.size()]);
    }

    /**
     * 返回一个类中的所有的属性名称.判别的方法是检查一个类的所有方法, 找到成对的 setter和getter方法,并根据这两个方法提取属性名称.
     * 
     * @param clazz
     *            Class 被检查的类
     * @return String[] 所有的属性名称
     */
    public static String[] getPropertyNames(Class clazz, boolean recursive) {
        Set<String> fieldNames = new HashSet<String>();
        String[] currentPropertNames = getPropertyNames(clazz);
        for (String propertyName : currentPropertNames) {
            fieldNames.add(propertyName);
        }

        if (recursive) {
            Class childClass = clazz;
            while (childClass != null && childClass != Object.class) {
                String[] propertNames = getPropertyNames(childClass);
                for (String propertyName : propertNames) {
                    if (fieldNames.contains(propertyName)) {
                        fieldNames.add(propertyName);
                    }
                }
                childClass = childClass.getSuperclass();
            }
        }

        return (String[]) fieldNames.toArray(new String[fieldNames.size()]);
    }

    /**
     * 返回一个类中的所有的属性名称.判别的方法是检查一个类的所有方法, 找到成对的 setter和getter方法,并根据这两个方法提取属性名称.
     * 
     * @param clazz
     *            Class 被检查的类
     * @return String[] 所有的属性名称
     */
    public static Class getFieldType(Class clazz, String fieldName) {
        String getterName = "get" + StringUtils.uppercaseCapital(fieldName);
        Method m = getMethod(clazz, getterName, NO_ARGS_CLASS);
        if (null != m) {
            return m.getReturnType();
        }

        // 属性有可能是Boolean类型
        String isName = "is" + StringUtils.uppercaseCapital(fieldName);
        m = getMethod(clazz, isName, NO_ARGS_CLASS);
        if (null != m) {
            return m.getReturnType();
        }

        return null;
    }

    /**
     * 判断一个方法是否是Getter方法
     * 
     * @return boolean
     */
    private static boolean isGetter(Method method) {
        if (method.getParameterTypes().length > 0) {
            return false;
        }
        if (method.getName().startsWith("get")) {
            return true;
        } else if (method.getName().startsWith("is")) {
            return (method.getReturnType() == boolean.class);
        }
        return false;
    }

    /**
     * 判断一个方法数组中是否存在Setter方法
     * 
     * @return boolean
     */
    private static boolean hasSetter(Method[] methods, Method getter) {
        for (int i = 0; i < methods.length; i++) {
            // setter方法以set开头,并且除了第一个字符以外与getter方法相同
            if (methods[i].getName().startsWith("set") && methods[i].getReturnType() == Void.TYPE
                    && methods[i].getName().substring(1).equals(
                    getter.getName().substring(1))) {
                return true;
            }
        }
        return false;
    }
    // /**
    // * Builds a new instance for the class represented by the given class
    // descriptor.
    // *
    // * @param cld The class descriptor
    // * @return The instance
    // */
    // public static Object buildNewObjectInstance (ClassDescriptor cld)
    // {
    // Object result = null;
    //
    // // If either the factory class and/or factory method is null,
    // // just follow the normal code path and create via constructor
    // if ((cld.getFactoryClass() == null) || (cld.getFactoryMethod() == null))
    // {
    // try
    // {
    // // 1. create an empty Object (persistent classes need a public default
    // constructor)
    // Constructor con = cld.getZeroArgumentConstructor();
    //
    // result = ConstructorHelper.instantiate(con);
    // }
    // catch (InstantiationException e)
    // {
    // throw new ClassNotPersistenceCapableException("Can't instantiate class '"
    // + cld.getClassNameOfObject() + "'");
    // }
    // }
    // else
    // {
    // try
    // {
    // // 1. create an empty Object by calling the no-parms factory method
    // Method method = cld.getFactoryMethod();
    //
    // if (Modifier.isStatic(method.getModifiers()))
    // {
    // // method is static so call it directly
    // result = method.invoke(null, null);
    // }
    // else
    // {
    // // method is not static, so create an object of the factory first
    // // note that this requires a public no-parameter (default) constructor
    // Object factoryInstance = cld.getFactoryClass().newInstance();
    //
    // result = method.invoke(factoryInstance, null);
    // }
    // }
    // catch (Exception ex)
    // {
    // throw new PersistenceBrokerException("Unable to build object instance of
    // class '"
    // + cld.getClassNameOfObject()
    // + "' from factory:" + cld.getFactoryClass()
    // + "." + cld.getFactoryMethod(), ex);
    // }
    // }
    // return result;
    // }
}
