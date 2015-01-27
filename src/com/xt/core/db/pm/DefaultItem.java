package com.xt.core.db.pm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.map.DBMapping;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;

/**
 * 将类结果与类自动进行一一映射。
 * <p>
 * Title: XT框架-持久化部分
 * </p>
 * <p>
 * Description:
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
 * @date 2006-4-10
 */
class DefaultItem implements Item {

	public Class clazz;

	private DBMapping mapping;

	public DefaultItem(Class clazz, DBMapping mapping) {
		this.clazz = clazz;
		this.mapping = mapping;
	}

	public Object createObject(ResultSet rs) throws SQLException {
		// 自动织入属性拷贝接口，加快属性的拷贝过程
		Object obj = ClassHelper.newInstance(clazz);

		// 结果元信息
		ResultSetMetaData rsmd = rs.getMetaData();

		// 字段的个数
		int count = rsmd.getColumnCount();

		// 所有的字段名称的集合
		Set<Key> columnNames = new HashSet<Key>(count);

		for (int i = 0; i < count; i++) {
			Key key = new Key(rsmd.getColumnName(i + 1));
			columnNames.add(key);
		}

		// 所有的可操作的属性值
		String[] propertyNames = ClassHelper.getPropertyNames(clazz);

		// 逐一赋值
		for (int i = 0; i < propertyNames.length; i++) {
			// 根据映射关系得到字段的名称
			String columnName = mapping.getColumnName(clazz, propertyNames[i]);

			// 只取一一对应的值
			// Key key = new Key(columnName);
			if (columnNames.contains(new Key(columnName))) {
				Object value = rs.getObject(columnName);

				Field field = ClassHelper.getField(clazz, propertyNames[i], true);

                // Blob 和 Clob 的查询处理同一用Converter进行转换
                
				// 类型不相等的情况下需要进行转换
				if (value != null
						&& field != null && field.getType() != value.getClass()) {
					Class sourceClass = value.getClass();
					Class targetClass = field.getType();
					Converter conv = ConverterFactory.getInstance().getConverter(sourceClass, targetClass);
					value = conv.convert(sourceClass, targetClass, value);
				}

				BeanHelper.copyProperty(obj, propertyNames[i], value);
			}
		}

		return obj;
	}

	public boolean isPagination() {
		return true;
	}

}

class Key {
	private String key;

	public Key(String key) {
		this.key = key.toUpperCase();
	}

	/**
	 * 一般情况下，bizHadler和method是不能为空的
	 * 
	 * @param pp
	 * @return 是否相等
	 */
	public boolean equals(Object obj) {
		return key.equals(obj.toString());
	}

	public int hashCode() {
		return key.hashCode();
	}

	public String toString() {
		return key;
	}
}
