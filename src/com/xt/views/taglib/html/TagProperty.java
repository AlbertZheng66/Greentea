package com.xt.views.taglib.html;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xt.core.log.LogWriter;

/**
 * <p>
 * Title: 读取标签的默认属性
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author zhengwei
 * @version 1.0
 */

public class TagProperty {
	
	/* 单例实例 */
	private static TagProperty instance;

	/* 文件的位置 */
	private final static String TAG_PROPERTIES = "taglib.properties";

	Properties properties = new Properties();

	private TagProperty() {
		readProperties();
	}

	public static TagProperty newInstance() {
		if (instance == null) {
			instance = new TagProperty();
		}
		return instance;
	}

	/**
	 * 读取属性值
	 * 
	 * @param key
	 *            关键字
	 * @return 返回关键字
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 读取属性文件
	 */
	private void readProperties() {
		try {
			InputStream is = TagProperty.class.getResourceAsStream(TAG_PROPERTIES);
			if (is != null) {
				properties.load(is);
			}
		} catch (IOException ex) {
			LogWriter.error("读取属性文件错误", ex);
		}
	}

}
