package com.xt.core.db.po.mapping;

import com.xt.core.db.po.Transient;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.xt.core.exception.BadParameterException;
import com.xt.core.map.DBMapping;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.StringUtils;
import java.lang.reflect.Field;

public class SimpleDBMapping implements DBMapping {
	
	private Logger logger = Logger.getLogger(SimpleDBMapping.class);

	/**
	 * 可处理（转换）的类型
	 */
	private Set<Class> types = new HashSet<Class>();

	// /**
	// * 记录与表相对应的类名称，在载入表信息时赋值。
	// */
	// private Class clazz;

	public SimpleDBMapping() {
		init();
	}

	private void init() {
		if (types.isEmpty()) {
			types.add(String.class);
			types.add(Integer.class);
			types.add(Long.class);
			types.add(Number.class);
			types.add(BigDecimal.class);
			types.add(Float.class);
			types.add(Double.class);
			types.add(Character.class);
			types.add(Byte.class);
			types.add(java.util.Date.class);
			types.add(java.sql.Date.class);
			types.add(Calendar.class);
			types.add(InputStream.class);
			types.add(Reader.class);
            types.add(byte[].class);
		}
	}

	public String getColumnName(final Class clazz, String propertyName) {
		boolean hasProperty = false;
		Class targetClass = clazz;
		while (targetClass != null && targetClass != Object.class) {
			try {
				targetClass.getDeclaredField(propertyName);
				hasProperty = true;
			} catch (Exception e) {
				// 如果当前类没有此属性，
				targetClass = clazz.getSuperclass();
				hasProperty = false;
			}
			if (hasProperty) {
				break;
			}
		}

		if (!hasProperty) {
			throw new BadParameterException("类[" + clazz.getName() + "]中未定义属性["
					+ propertyName + "]");
		}
		// load(clazz);
		return StringUtils.camel2Underline(propertyName);
	}

	// private void load(Class clazz) {
	// validate(clazz);
	//		
	// // 未装载或者装载的表发生了变化时需要重新载入
	// if (table != null && this.clazz == clazz) {
	// return;
	// }
	//		
	// this.clazz = clazz;
	// table = new Table(null, getTableName(clazz));
	// String[] pns = ClassHelper.getPropertyNames(clazz);
	// if (pns == null || pns.length == 0) {
	// throw new BadParameterException("类[" + clazz + "]未定义可读取的属性！");
	// }
	// Column[] columns = new Column[pns.length];
	// for (int i = 0; i < columns.length; i++) {
	// columns[i] = getColumn(table, clazz, pns[i]);
	// }
	// table.setColumns(columns);
	// }

	// private Column getColumn(Table table, Class clazz, String propertyName) {
	// Field field = ClassHelper.getField(clazz, propertyName);
	// //
	// if (field == null) {
	// throw new BadParameterException("未发现类[" + clazz + "]的属性["
	// + propertyName + "]");
	// }
	// Column column = new Column(table, StringUtils
	// .camel2Underline(propertyName));
	// if ("id".equals(propertyName)) {
	// column.setPrimaryKey(true); // 主键默认为ID
	// Column[] pks = new Column[1];
	// pks[0] = column;
	// table.setPks(pks);
	// }
	// column.setDec(0);
	// if (int.class == field.getType() || Integer.class == field.getType()) {
	// column.setType("NUMBERIC");
	// column.setSqlType(Types.INTEGER);
	// column.setWidth(12);
	// column.setPrimaryKey("id".equals(propertyName)); // 主键默认为ID
	// } else if (long.class == field.getType() || Long.class ==
	// field.getType()) {
	// column.setType("NUMBERIC");
	// column.setSqlType(Types.INTEGER);
	// column.setWidth(20);
	// } else if (double.class == field.getType() || Double.class ==
	// field.getType()) {
	// column.setType("NUMBERIC");
	// column.setSqlType(Types.DOUBLE);
	// column.setWidth(38);
	// column.setDec(38);
	// } else if (float.class == field.getType() || Float.class ==
	// field.getType()) {
	// column.setType("NUMBERIC");
	// column.setSqlType(Types.FLOAT);
	// column.setWidth(12);
	// column.setDec(10);
	// } else if (String.class == field.getType() || Character.class ==
	// field.getType()
	// || Byte.class == field.getType() || char.class == field.getType()
	// || byte.class == field.getType()) {
	// column.setType("VARCHAR2");
	// column.setSqlType(Types.VARCHAR);
	// column.setWidth(127);
	// } else if (java.util.Date.class == field.getType()
	// || java.util.Calendar.class == field.getType()) {
	// column.setType("DATE");
	// column.setSqlType(Types.DATE);
	// }
	// return column;
	// }

	/**
	 * 目前，只能处理类的String,基础类型及其包装类， 日期（java.sql.Date,Calendar,java.util.Date）
	 * 以及流（InputStream，Reader）等类型。
	 */
	public String[] getPropertyNames(Class clazz) {
		if (clazz == null) {
			throw new BadParameterException("类不能为空！");
		}

		String[] propertyNames = ClassHelper.getPropertyNames(clazz);
		// 要有可以持久化的属性
		if (propertyNames == null || propertyNames.length == 0) {
			throw new BadParameterException("类[" + clazz + "]未定义可持久化的属性！");
		}

		// 读取可以转换的属性
		List<String> listPns = new ArrayList<String>(propertyNames.length);
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
                        
                        Field field = ClassHelper.getField(clazz, name);
                        // 忽略临时变量
                        if (field != null && field.getAnnotation(Transient.class) != null) {
                            continue;
                        }

			Class<?> fieldType = ClassHelper.getFieldType(clazz, name);
			if (fieldType.isPrimitive() || types.contains(fieldType) || fieldType.isEnum()) {
				listPns.add(name);
			}
			//LogWriter.debug(logger, clazz + "; getPropertyNames name", name);
			
		}
		
		return (String[]) listPns.toArray(new String[listPns.size()]);
	}

	public String getTableName(Class clazz) throws BadParameterException {
		validate(clazz);
		String tableName = clazz.getSimpleName();
		return StringUtils.camel2Underline(tableName);
	}

	/**
	 * 校验类名称是否合法，如果不合法，则抛出非法参数异常。
	 * 
	 * @param clazz
	 */
	private void validate(Class clazz) {
		if (clazz == null) {
			throw new BadParameterException("类名不能为空");
		}
		if (clazz.isArray() || clazz.isPrimitive() || clazz.isEnum()) {
			throw new BadParameterException("原始类型、数组类型和枚举类型不能作为表名称");
		}
	}

	// public Table getTable(Class clazz) {
	// // load(clazz);
	// return this.table;
	// }

	public void setSuccessor(DBMapping dbMapping) {

	}

}
