package com.xt.core.service.impl;

import java.lang.reflect.Modifier;
import java.util.Map;

import com.xt.core.service.IService;
import com.xt.core.service.ServiceLoader;
import com.xt.gt.sys.SystemConfiguration;

/**
 * 按照约定进行处理的服务类装载器。如果服务类是接口或者抽象类，则使用其名称加后缀查找
 * 其相应的实现类，如果未找到，则返回空。如果类既非接口又不是抽象类，则返回其本身。<br/>
 * <br/>
 * 例如：约定的后缀是“Impl”，传入的接口为：“com.xxx.yyy.BusinessService”，则首先查找
 * 其实现类“com.xxx.yyy.BusinessServiceImpl”，如果未找到，则返回空。
 * 
 * @author albert
 * 
 */
public class RuleServiceLoader implements ServiceLoader {
	
	/**
	 * 约定服务装载器的后缀的参数名称。
	 */
	private static final String RULE_SERVICE_LOADER_SUBFIX = "ruleServiceLoader.subfix";
	
	/**
	 * 实现类的默认后缀。如果接口定义为：com.xxx.yyy.BusinessService，
	 * 则其实现类为：com.xxx.yyy.BusinessServiceImpl。
	 */
	private final String subfix = SystemConfiguration.getInstance().readString(RULE_SERVICE_LOADER_SUBFIX, "Impl");

	/**
	 * 按照约定规则（参加类注释）返回其实现类。如果未找到，则返回空。
	 */
	@SuppressWarnings("unchecked")
	public <T extends IService> Class<? extends T> findClass(Class<T> clazz) {
		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			String implClassName = clazz.getName() + subfix;
			Class target = null;
			try {
				target = Class.forName(implClassName);
			} catch (ClassNotFoundException e) {
				// ignore this exception;
			}
			if (target != null && clazz.isAssignableFrom(target)) {
				return target;
			}
		} else {
			return clazz;
		}
		return null;
	}

	public <T extends IService> Map<Class<T>, Class<? extends T>> loadClasses() {
		return null;
	}

}
