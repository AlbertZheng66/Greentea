package com.xt.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.xt.core.service.LocalMethod;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;

public class ServiceHelper {
	static public void copyLocalProperites(Object dest, Object source) {
		if (dest == null || source == null) {
			return;
		}
		Class<?> clazz = dest.getClass();
		Set<Method> localMethods = new HashSet<Method>();
		for (Method method : clazz.getMethods()) {
			LocalMethod lmAnno = method.getAnnotation(LocalMethod.class);
			if (lmAnno != null) {
				localMethods.add(method);
			}
		}

		String[] pns = ClassHelper.getPropertyNames(clazz);
		for (String propertyName : pns) {
			Field field = ClassHelper.getField(clazz, propertyName);
			// 非持久化变量
			if (field != null && Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			// 暂时未处理 boolean 属性。
			Method getter = ClassHelper.getGetterMethod(clazz, propertyName);
			if (!localMethods.contains(getter)) {
				continue;
			}
			Method setter = ClassHelper.getSetterMethod(clazz, propertyName);
			if (!localMethods.contains(setter)) {
				continue;
			}
			// 当属性的Setter和Getter都是本地属性时，则使用复制属性
			Object value = BeanHelper.getProperty(source, propertyName);
			BeanHelper.copyProperty(dest, propertyName, value);
		}
	}
}
