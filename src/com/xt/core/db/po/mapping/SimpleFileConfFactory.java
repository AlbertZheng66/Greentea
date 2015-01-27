package com.xt.core.db.po.mapping;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.xt.core.exception.BadParameterException;

/**
 * <p>
 * Title: XT框架-持久化部分
 * </p>
 * <p>
 * Description: FileConfFactory负责载入与每个持久化类相关的配置文件， 并存放在一个FileConf对象中。
 * 他是FileConf的管理类，对FileConf进行加载和缓存， 使用单件模式避免重复装载。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-4-15
 */

public class SimpleFileConfFactory {
	
	/**
	 * 没有配置文件的类都采用这种类，避免重复加载类的配置文件。
	 */
	private final static SimpleFileConf NO_MAPPING_FILE = new SimpleFileConf();

	/**
	 * 单件实例
	 */
	private static SimpleFileConfFactory instance = new SimpleFileConfFactory();

	/**
	 * 映射文件后缀，配置文件命名规则是类名称+后缀，比如：类SimpleFileConf的配置文件名是 SimpleFileConf.spm.xml。
	 */
	private final static String MAPPING_SUBFIX = ".spm.xml";

	/**
	 * 存放已经加载的FileConf类
	 */
	private Map repo = new HashMap();

	/**
	 * 实例化方法。
	 * 
	 * @return
	 */
	public static SimpleFileConfFactory newInstance() {
		return instance;
	}

	/**
	 * 返回指定类的文件配置，如果此类没有文件配置，则返回空。
	 * 
	 * @param clazz
	 * @return
	 */
	public SimpleFileConf getConfMapping(Class clazz) {
		if (null == clazz) {
			throw new BadParameterException("参数不能为空！");
		}
		
		SimpleFileConf hm = (SimpleFileConf) repo.get(clazz);
		
		//如果没有配置文件，避免重复加载配置文件
		if (NO_MAPPING_FILE == hm) {
			return null;
		}
		
		//同步处理，避免多线程时出现问题
		synchronized (SimpleFileConfFactory.class) {
			if (hm == null) {
				// 目前，hbm的格式采用Hibernate3的格式
				InputStream is = clazz.getResourceAsStream(clazz
						.getSimpleName()
						+ MAPPING_SUBFIX);

				/**
				 * @todo 应该针对“流”的格式进行校验，使用Schema进行校验。
				 */

				// 配置文件可能不存在
				if (is != null) {
					hm = new SimpleFileConf();
					hm.load(is);
					//保存在配置库中
					repo.put(clazz, hm);
				} else {
					//保存在配置库中
					repo.put(clazz, NO_MAPPING_FILE);
				}
			}
		}
		return hm;
	}
}
