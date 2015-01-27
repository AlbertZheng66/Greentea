package com.xt.core.db.po.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;
import com.xt.core.map.DBMapping;

/**
 * 根据配置文件得到数据库映射。
 * <p>
 * Title: XT框架-文件映射部分
 * </p>
 * <p>
 * Description:如果类有对应的配置文件，则其所包含或排除的属性是依赖于后继者的。
 * 如果配置文件中定义了表的名称，则返回之；否则，返回后继者的表名称。
 * 如果无后继者，则类需要持久化的属性只有包含的属性。如果有后继者，则类文件中被
 * 排除的属性将从后继者提高的类属性中排除；而类文件自定义包含的属性，则使用
 * 自定义的属性，而不是使用后继者的属性。
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
 * @date 2006-9-3
 */
public class FileDBMapping implements DBMapping {

	private DBMapping successor;

	public FileDBMapping() {
	}

	public String getColumnName(Class clazz, String propertyName) {
		SimpleFileConf fileConf = SimpleFileConfFactory.newInstance().getConfMapping(
				clazz);
		String columnName = null;
		
		if (fileConf != null) {
			columnName = fileConf.findColumnName(propertyName);
		} 
		
		if (columnName == null && successor != null) {
			columnName = successor.getColumnName(clazz, propertyName);
		}
		
		if (columnName == null) {
			throw new BadParameterException("类[" + clazz.getName() 
					+ "]没有定义属性[" + propertyName + "]或者属性被自定义的文件排除");
		}
		return columnName;
	}

	public String[] getPropertyNames(Class clazz) {
		// 首先查找类是否有相关的配置文件
		SimpleFileConf fileConf = SimpleFileConfFactory.newInstance().getConfMapping(
				clazz);

		Set includes = new HashSet(); // 已经被配置文件包含的字段
		Set excludes = new HashSet(); // 已经被配置文件排除的字段

		//得到排除和包含属性的字段
		if (fileConf != null) {
			com.xt.core.db.po.mapping.Property[] hps = fileConf.getProperties();
			for (int i = 0; i < hps.length; i++) {
				com.xt.core.db.po.mapping.Property hiProperty = hps[i];
				String propertyName = hiProperty.getName();
				if (hps[i].isExclude()) {
					excludes.add(propertyName); // 不需要持久化的字段
				} else {
					includes.add(propertyName); // 自定义对应关系的持久化字段
				}
			}
		}

		// 属性数组
		String[] propertyNames = new String[0];
		List propertyNameList = new ArrayList(); // 未被排除的属性
		
		if (successor != null) {
			propertyNames = successor.getPropertyNames(clazz);
		}
		
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			if (!excludes.contains(propertyName)) { 
				//用户自定义了属性
				if (includes.contains(propertyName)) {
					propertyNameList.add(fileConf.findColumnName(propertyName));
				} else {
				    propertyNameList.add(propertyName);
				}
			}
		}

		// 如果没有属性，则抛出参数错误
		if (propertyNameList.isEmpty()) {
			throw new BadParameterException("类[" + clazz.getName()
					+ "]没有持久化属性");
		} 
		return ((String[])propertyNameList.toArray(new String[0]));
	}

	/**
	 * 如果配置文件里定义了并名称，则采用此表名称；如果没有自定义的表名称，
	 * 但是定义了后继者，则从后继者处取得的名称，
	 */
	public String getTableName(Class clazz) {
		SimpleFileConfFactory factory = SimpleFileConfFactory.newInstance();
		SimpleFileConf fileConf = factory.getConfMapping(clazz);
        //如果配置文件没有定义这个表的名称，则由其后继者提高表的名称
		if (fileConf != null && StringUtils.isNotEmpty(fileConf.getTableName())) {
			return fileConf.getTableName();
		} else if (successor != null) {
			return successor.getTableName(clazz);
		} else {
			throw new BadParameterException("未能得到类[" + clazz.getName()
					+ "]对应的表名称");
		}
	}

	public void setSuccessor(DBMapping dbMapping) {
		this.successor = dbMapping;
	}

}
