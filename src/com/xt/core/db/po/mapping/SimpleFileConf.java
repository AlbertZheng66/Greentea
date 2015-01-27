package com.xt.core.db.po.mapping;

import java.io.InputStream;
import java.util.List;

import org.jdom.Element;

import com.xt.core.exception.BadParameterException;
import com.xt.core.utils.XmlHelper;

/**
 * 简化的配置文件。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 针对每个持久对象配置一个文件，包含了属性最基本的属性。有几种包含关系：
 * 1，包含，当属性名称和字段名称不相对应时，采用“包含”属性；如果一个字段没有定义，则默认采用“包含”属性。
 * 2，排除，当不需要持久化某个字段属性时，采用排除属性。
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
 * @date 2006-9-5
 */
public class SimpleFileConf {

	// 对应的表名称，可以省略（即按照默认规则算）
	private String tableName;

	private Property[] properties;

	public SimpleFileConf() {

	}

	/**
	 * 负责装载Hibernate映射的属性，但是不负责校验XML的格式，必添属性等信息，由调用其的类校验。
	 * 
	 * @param xml
	 */
	public void load(InputStream xml) {
		Element root = XmlHelper.getRoot(xml);

		// 类节点
		Element classNode = root.getChild("class");

		tableName = classNode.getAttributeValue("table");

		/**
		 * 配置的模式：include=0;exclude=1
		 */
		String classPattern = classNode.getAttributeValue("pattern");

		// 读取所有属性
		List propertyNodes = classNode.getChildren("property");
		if (propertyNodes != null && propertyNodes.size() > 0) {
			properties = new Property[propertyNodes.size()];
			for (int i = 0; i < properties.length; i++) {
				Property p = new Property();
				Element pNode = (Element) propertyNodes.get(i);
				p.setName(pNode.getAttributeValue("name"));

				String pattern = classPattern; // 属性的模式

				// 如果没有配置类级别的属性则使用字段级别的属性，默认为包含
				if (classPattern == null) {
					pattern = pNode.getAttributeValue("pattern");
				}
				p.setExclude("exclude".equalsIgnoreCase(pattern));
				p.setColumnName(pNode.getAttributeValue("column"));
				properties[i] = p;
			}
		}
	}

	/**
	 * 根据属性名称得到其相应的字段名称
	 * 
	 * @param propertyName
	 *            属性名称
	 * @return
	 */
	public String findColumnName(String propertyName) {
		String columnName = null;
		if (properties != null && null != propertyName) {
			for (int i = 0; i < properties.length; i++) {
				if (propertyName.equals(properties[i].getName())) {
					if (properties[i].isExclude()) {
						throw new BadParameterException("属性[" + propertyName
								+ "]已经被自定义的文件排除");
					}
					columnName = properties[i].getColumnName();
				}
			}
		}
		return columnName;
	}

	public Property[] getProperties() {
		return properties;
	}

	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
