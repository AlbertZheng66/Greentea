package com.xt.core.service;

import java.util.Map;

/**
 * 
 * 服务类装载器，用于建立服务接口与服务之间的映射关系。
 * 
 * @author albert
 *
 */
public interface ServiceLoader {
	
	/**
	 * 返回所有的类映射关系，注册时调用此方法。一般适用于有初始化的情况。
	 * @return
	 */
	public <T extends IService> Map<Class<T>, Class<? extends T>>  loadClasses();
	
	/**
	 * 如果当前缓存池没有找到对应的服务器，则使用此方法进行动态查找。
	 * @param clazz 服务类参数，不为空。
	 * @return 对应的参数，如果未找到，则返回空。
	 */
	public <T extends IService> Class<? extends T> findClass(Class<T> clazz);

}
