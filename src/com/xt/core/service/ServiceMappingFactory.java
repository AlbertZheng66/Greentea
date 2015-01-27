package com.xt.core.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.xt.core.exception.SystemException;
import com.xt.core.service.impl.RuleServiceLoader;
import com.xt.core.service.impl.XmlServiceLoader;
import com.xt.gt.sys.SystemConfiguration;

public class ServiceMappingFactory {

	/**
	 * 系统参数：服务器加载类，要求其是一个“列表”参数。
	 */
	private static final String SERVICE_LOADERS = "serviceLoaders";

	private static ServiceMappingFactory instance;

	private Map<Class<? extends IService>, Class<? extends IService>> pool = new Hashtable<Class<? extends IService>, Class<? extends IService>>();

	private List<ServiceLoader> loaders = new ArrayList<ServiceLoader>();

	static {
		instance = new ServiceMappingFactory();
		ServiceLoader[] loaders = (ServiceLoader[])SystemConfiguration.getInstance().readObjects(SERVICE_LOADERS, ServiceLoader.class);
		if (loaders != null && loaders.length > 0) {
			for (ServiceLoader loader : loaders) {
				instance.register(loader);
			}
		} else {
			// 默认加载“约定服务器加载类”
			instance.register(new XmlServiceLoader());
			instance.register(new RuleServiceLoader());
		}
	}
	
	private ServiceMappingFactory() {
	}

	static public ServiceMappingFactory getInstance() {
		return instance;
	}

	/**
	 * 注册一个服务装载器，如果传入参数为空，则不进行任何处理。
	 * 
	 * @param loader
	 */
	@SuppressWarnings("unchecked")
	synchronized public void register(ServiceLoader loader) {
		if (loader != null) {
			Map mapping = loader.loadClasses();
			if (mapping != null) {
				pool.putAll(mapping);
			}
			if (!loaders.contains(loader)) {
			    loaders.add(loader);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends IService> Class<? extends T> getClass(Class<T> clazz) {
		if (clazz == null) {
			throw new SystemException("查找类不能为空。");
		}
		Class targetClazz = null;
		targetClazz = pool.get(clazz);
		if (targetClazz == null) {
			for (ServiceLoader loader : loaders) {
				targetClazz = loader.findClass(clazz);
				if (targetClazz != null) {
					pool.put(clazz, targetClazz);
					break;
				}
			}
			if (targetClazz == null) {
				throw new SystemException(String.format("类[%s]的映射为空。", clazz
						.getName()));
			}
		}
		return (Class<? extends T>) targetClazz;
	}

}
