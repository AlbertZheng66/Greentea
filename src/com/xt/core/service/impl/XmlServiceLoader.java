package com.xt.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.xt.core.exception.SystemException;
import com.xt.core.service.IService;
import com.xt.core.service.ServiceLoader;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.SystemConfiguration;

public class XmlServiceLoader  implements ServiceLoader{

	public <T extends IService> Class<? extends T> findClass(Class<T> clazz) {
		return null;
	}

	public <T extends IService> Map<Class<T>, Class<? extends T>> loadClasses() {
		Map<String, String> sm = SystemConfiguration.getInstance().readMap("serviceMapping");
		if (sm != null) {
			Map<Class<T>, Class<? extends T>> mapping = new HashMap<Class<T>, Class<? extends T>>();
			for(Map.Entry<String, String> entry : sm.entrySet()) {
				Class keyClass = ClassHelper.getClass(entry.getKey());
				Class valueClass = ClassHelper.getClass(entry.getValue());
				if (!IService.class.isAssignableFrom(keyClass)) {
					throw new SystemException(String.format("类[%s]必须实现接口[%s]。", keyClass.getName(), IService.class.getName()));
				}
				if (!keyClass.isAssignableFrom(valueClass)) {
					throw new SystemException(String.format("类[%s]必须继承（或实现）类（接口）[%s]。", valueClass.getName(),
							keyClass.getName()));
				}
				mapping.put(keyClass, valueClass);
			}
			return mapping;
		}
		return null;
	}

}
