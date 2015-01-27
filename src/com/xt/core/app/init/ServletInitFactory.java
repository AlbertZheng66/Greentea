package com.xt.core.app.init;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.ServletContext;

import com.xt.core.exception.SystemException;
import com.xt.core.utils.ClassHelper;

/**
 * @deprecated 
 *
 * @author albert
 */
public class ServletInitFactory {

	public ServletInitFactory() {
	}
	
	public void init (ServletContext servletContext) {

	}

	public ServletInit newInstance(String className) {
        //如果类未发现，则直接抛出异常
		Class clazz = ClassHelper.getClass(className);
		Object obj = null;

		//尝试采用公共构造函数实例化类
		Constructor[] constructors = clazz.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Constructor cons = constructors[i];
			if (Modifier.isPublic(cons.getModifiers())
					&& cons.getParameterTypes().length == 0) {
				// 此类存在公共的构造函数
				obj = ClassHelper.newInstance(clazz);
				break;
			}
		}

        //如果系统没有公共构造函数，则查看其是否有静态的、
		// 无参数的”newInstance“或者”getInstance“方法，
		// 并使用这个方法进行初始化
		if (obj == null) {
			Method[] methods = clazz.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (("newInstance".equals(method.getName())
						|| "getInstance".equals(method.getName()))
						&& Modifier.isPublic(method.getModifiers())
						&& method.getParameterTypes().length == 0
						&& Modifier.isStatic(method.getModifiers())) {
					try {
						obj = method.invoke(null, new Object[] {});
					} catch (IllegalArgumentException e) {
						throw new SystemException("实例化类[" + className + "]失败", e);
					} catch (IllegalAccessException e) {
						throw new SystemException("实例化类[" + className + "]失败", e);
					} catch (InvocationTargetException e) {
						throw new SystemException("实例化类[" + className + "]失败", e);
					}
					break;
				}
			}
		}

		if (obj == null) {
			throw new SystemException("实例化类[" + className + "]失败");
		}

		//将对象转换为ServletInit对象
		ServletInit servInit = null;

		if (obj instanceof ServletInit) {
		    servInit = (ServletInit)obj;
		} else {
			throw new SystemException("类[" + className + "]没有实现ServletInit接口");
		}
		return servInit;
	}
}
