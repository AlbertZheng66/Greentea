package com.xt.core.db.po.mapping;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xt.core.db.meta.Column;
import com.xt.core.db.meta.Database;
import com.xt.core.db.meta.Table;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.map.DBMapping;
import com.xt.core.map.MappingClass;
import com.xt.core.map.Property;
import com.xt.core.utils.Assert;
import com.xt.core.utils.ClassHelper;

/**
 * <p>
 * Title: XT框架-持久化部分.
 * </p>
 * <p>
 * 首先从配置文件中读取自定义的映射方式，并按照责任链的方式按照配置的顺序配置。如果配置文件
 * 中没有指定映射方式，则系统将采用FileDBMapping和SimpleDBMapping两级责任链方式进行处理。
 * DefaultDBMapping类本身并不处理映射关系，其只是责任链调度的开始。为了避免多次载入， 系统将对应关系在此缓存。
 * </p>
 * <p>
 * Description: 缺省的数据库映射关系，可以无需借助任何配置文件，即可建立其映射关系。映射的规则是：首先查找与类 同名（类名+
 * ".hbm.xml"）的配置文件，如果找到，读取其中的属性（可能只是部分属性）及其对于关系。如果未发现
 * 配置文件，则采用默认的映射规则。即简单的下划线和骆驼记法转换规则。在Java类中采用下划线记法，而在其对应的数据库
 * 字段和表中采用下划线记法。比如：Java中的属性tableName对应于数据库字段是:TABLE_NAME。
 * </p>
 * 根据表和字段的名称，从数据库和检索出相应的元信息，如：类型，长度，精度等。
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * FIXME: 注意：需要考虑多线程同步的问题。
 * @author 郑伟
 * @version 1.0
 */

public class DefaultDBMapping implements DBMapping {

	/**
	 * 映射缓存,主键是类类型(Class),键值是MappingClass的对象
	 */
	private static Map<Class, MappingClass> mappings = new Hashtable<Class, MappingClass>();

	private DBMapping successor;

	public DefaultDBMapping() {

	}

	/**
	 * 根据默认规则装载类信息
	 * 
	 * @param calzz
	 */
	private MappingClass load(Class clazz) {

		loadSuccessor();

		// 参数不能为空
		Assert.businessAssert(clazz != null, new BadParameterException(
				"映射类不能为空！"));

		MappingClass mappingClass = new MappingClass();
		mappingClass.setName(clazz.getName());

		// 将类名转换为数据库名称
		String tableName = successor.getTableName(clazz);

		// 现在没有在表中存放字段的信息
		Database db = Database.getInstance();
		Table table = db.find(null, tableName);
		if (table == null) {
			throw new SystemException("类[" + clazz + "]在数据库中没有定义相应的表!");
		}

		mappingClass.setTable(table);

		// 所有属性的名称,从数据库元信息中读到字段信息
		String[] pns = successor.getPropertyNames(clazz);
		for (int i = 0; i < pns.length; i++) {
			String name = pns[i]; // 属性名称
			Property p = new Property();
			p.setName(name);
			// 从类中得到属性元信息
			Class javaType = ClassHelper.getFieldType(clazz, name);
			p.setType(javaType.getName());

			// 从数据库中得到字段的元信息
			String columnName = successor.getColumnName(clazz, name);
			Column col = table.find(columnName);
			if (col == null) {
				throw new SystemException("类[" + clazz + "]的属性[" + columnName
						+ "]在数据库中没有定义相应的表!");
			}
			p.setColumn(col);

			// 追加属性
			mappingClass.addProperty(p);
		}

		// 存放到缓存中
		mappings.put(clazz, mappingClass);

		return mappingClass;
	}

	/**
	 * 
	 */
	private void loadSuccessor() {
		// 系统默认采用的映射方式
		if (successor == null) {
			successor = new FileDBMapping();
			// 设置后继者
			successor.setSuccessor(new SimpleDBMapping());
		}
	}

	public Column getColumn(Class clazz, String propertyName) {
		Column column = null;
		// 首先在缓存中查找
		MappingClass mc = getMappingClass(clazz);
		if (null == mc) {
			throw new SystemException("类[" + clazz.getName() + "]未发现对应的映射!");
		}

		// 如果发现需要的字段名称便终止查找
		for (Iterator iter = mc.getProperties().iterator(); iter.hasNext();) {
			Property property = (Property) iter.next();
			if (propertyName.equals(property.getName())) {
				column = property.getColumn();
				break;
			}
		}
		if (null == column) {
			throw new SystemException("类[" + clazz.getName() + "]的["
					+ propertyName + "]未发现对应的映射!");
		}
		return column;
	}

	/**
	 * 返回给指定类的所有的属性名称.默认的查找顺序是:1,首先在类的映射文件中查找,映射文件是与指定的类文件
	 * 同名,但于map.xml结尾的XML文件;如果未发现映射文件,则使用ClassHelper的查找方法,根据属性Setter
	 * 和Getter方法得到其属性名称.
	 * 
	 * @param clazz
	 *            Class
	 * @return String[]
	 */
	public String[] getPropertyNames(Class clazz) {
		String[] pns = null;
		List<String> pnsList = new ArrayList<String>();
		// 首先在缓存中查找
		MappingClass mc = getMappingClass(clazz);
		if (mc != null) {
			for (Iterator iter = mc.getProperties().iterator(); iter.hasNext();) {
				Property property = (Property) iter.next();
				pnsList.add(property.getName());
			}
			pns = new String[pnsList.size()];
			pns = (String[]) pnsList.toArray(pns);
		} else {
			pns = successor.getPropertyNames(clazz);
			load(clazz);
		}
		return pns;
	}

	public String getTableName(Class clazz) {
		MappingClass mappingClass = getMappingClass(clazz);
		return mappingClass.getTable().getName();
	}

	private MappingClass getMappingClass(Class clazz) {
		MappingClass mc = null;
		if (!mappings.containsKey(clazz)) {
			mc = load(clazz);
		} else {
			mc = (MappingClass) mappings.get(clazz);
		}
		return mc;
	}

	public Column[] getColumns(Class clazz) {
		MappingClass mc = getMappingClass(clazz);
		List<Column> cols = new ArrayList<Column>(mc.getProperties().size());
		for (Iterator iter = mc.getProperties().iterator(); iter.hasNext();) {
			Property property = (Property) iter.next();
			cols.add(property.getColumn());
		}
		return (Column[]) cols.toArray(new Column[0]);
	}

	/**
	 * 在大写字符字母前面填写下划线,然后将全部字符转换为大写。
	 */
	public String getColumnName(Class clazz, String propertyName) {
		loadSuccessor();
		return successor.getColumnName(clazz, propertyName);
	}

	public Table getTable(Class clazz) {
		// 首先在缓存中查找
		MappingClass mc = getMappingClass(clazz);
		if (null == mc) {
			throw new SystemException("类[" + clazz.getName() + "]未发现对应的映射!");
		}
		return mc.getTable();
	}

	public void setSuccessor(DBMapping dbMapping) {

	}

}
