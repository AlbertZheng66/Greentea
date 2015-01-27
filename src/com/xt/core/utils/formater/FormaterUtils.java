package com.xt.core.utils.formater;

import java.util.Hashtable;
import java.util.Map;

import com.xt.core.exception.BadParameterException;

public class FormaterUtils {

	private Map<Class, Formater> repos = new Hashtable<Class, Formater>();

	private static FormaterUtils instance;

	static {
		instance = new FormaterUtils();
	}

	private FormaterUtils() {
	}

	public static FormaterUtils getInstance() {
		return instance;
	}

	/**
	 * 注册一个格式化器，如果已经对一个类型注册了，则第二次注册的将不成功
	 * 
	 * @param clazz
	 * @param formater
	 */
	public synchronized void register(Class clazz, Formater formater) {
		if (clazz == null || formater == null) {
			throw new BadParameterException("注册参数不能为空");
		}
		if (!repos.containsKey(clazz)) {
			repos.put(clazz, formater);
		}
	}

	public Formater get(Class clazz) {
		if (clazz == null) {
			throw new BadParameterException("注册参数不能为空");
		}
		return (Formater) repos.get(clazz);
	}
}
