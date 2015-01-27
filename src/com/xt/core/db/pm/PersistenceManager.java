package com.xt.core.db.pm;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import com.xt.core.db.conn.DatabaseContext;

/**
 * <p>
 * Title: 持久化管理器接口。
 * <p>
 * Description: 封装了持久化需要的基本接口，包括：增加、
 * </p>
 * 删除、修改和基本查询。增加、删除、修改这三个操作不包含类的属性的持久化（不保证）。查询可以包括 类的属性的装载过程。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public interface PersistenceManager {

	/**
	 * 查询结果最多的数量
	 */
	public static final int MAX_QUERY_RESULT_ITEMS = 10000;
	
	
	// 注入数据库环境
	public void setDatabaseContext(DatabaseContext dc);

	/**
	 * 设置起始页位置，用于翻页操作，0表示第 1 行
	 * 
	 * @param startIndex
	 */
	public void setStartIndex(int startIndex);

	/**
	 * 设置每页，用于翻页操作
	 * 
	 * @param fetchSize
	 */
	public void setFetchSize(int fetchSize);

	public long count(Class clazz, String sqlWhere, List params);

	public void beginTransaction() throws PersistenceException;

	public void commit() throws PersistenceException;

	public void rollback() throws PersistenceException;

	public void setAutoCommit(boolean autoCommit) throws PersistenceException;

	/**
	 * 插入操作
	 * 
	 * @param newObject
	 *            IPersistence 插入的实例（包含数据的JavaBean）
	 * @throws PersistenceException
	 *             持久化异常
	 * @return boolean
	 */
	public boolean insert(IPersistence newObject) throws PersistenceException;

	/**
	 * 更新操作
	 * 
	 * @param newObject
	 *            IPersistence 更新后的实例（包含数据的JavaBean）
	 * @param oldObject
	 *            IPersistence 更新前的实例（包含数据的JavaBean）可以为空，这时可以通过 主键进行对象的辨别。
	 * @throws PersistenceException
	 *             持久化异常
	 * @return boolean
	 */
	public boolean update(IPersistence obj) throws PersistenceException;

	/**
	 * 更新操作
	 * 
	 * @param newObject
	 *            IPersistence 更新后的实例（包含数据的JavaBean）
	 * @param oldObject
	 *            IPersistence 更新前的实例（包含数据的JavaBean）可以为空，这时可以通过 主键进行对象的辨别。
	 * @throws PersistenceException
	 *             持久化异常
	 * @return boolean
	 */
	public boolean update(IPersistence oldObject, Serializable key)
			throws PersistenceException;

	/**
	 * 删除操作
	 * 
	 * @param object
	 *            IPersistence 插入的实例（包含数据的JavaBean）
	 * @throws PersistenceException
	 *             持久化异常
	 * @return boolean
	 */
	public boolean delete(IPersistence object) throws PersistenceException;

//	/**
//	 * 批量删除操作，读取object对象中变化过的值主从SQL语句， 然后批量删除符合条件的对象。
//	 * 
//	 * @param object
//	 *            IPersistence 插入的实例（包含数据的JavaBean）
//	 * @throws PersistenceException
//	 *             持久化异常
//	 */
//	public void deleteAll(IPersistence object) throws PersistenceException;

	/**
	 * 通过主键查找,主键的值直接被赋予对象，因此，这种方式比较适合符合主键的情况。
	 * 
	 * @param object
	 *            IPersistence 插入的实例（包含数据的JavaBean），主键不能为空
	 * @throws PersistenceException
	 *             持久化异常
	 * @return Object 如果未找到对象，返回空。
	 */
	public Object findByPK(IPersistence object) throws PersistenceException;

	/**
	 * 通过主键查找相应的对象，如果主键为空，抛出空主键异常。如果类没有默认的公共构造函数，将抛出系统异常。 这种方式只能支持单主键的情况。
	 * 
	 * @param clazz
	 *            要查找的类型
	 * @param pk
	 *            主键
	 * @return 如果找到了，返回要查找的类型的实例，否则返回空。
	 * @throws PersistenceException
	 *             数据库出现问题时，此异常（也可能由程序错误引起）。
	 */
	public <T> T findByPK(Class<T> clazz, Serializable pk)
			throws PersistenceException;

	/**
	 * 这个接口提供了最简单的方法进行基于单个对象的查找。 也就是说，查询语句是基于这个类的，而不能关联其它的类。
	 * 
	 * @param clazz
	 *            IPO 类,对应数据库的一个表。
	 * @param sqlWhere
	 *            查询语句，此语句采用的形式是基本的Where语句的形式，字段变量采用类属性的名称。所有的 查询变量都是以参数的形式传入。
	 * @param params
	 *            查询变量的参数
	 * @param sqlOrder
	 *            排序。可以为空，表示无序排序。
	 * @return 查询到的结果，如果无结果，返回长度为0的数组。
	 * @throws PersistenceException
	 */
	public <T> List<T> findAll(Class<T> clazz, String sqlWhere, List params,
			String sqlOrder) throws PersistenceException;
        
        /**
	 * 这个接口提供了最简单的方法进行基于单个对象的查找。 也就是说，查询语句是基于这个类的，而不能关联其它的类。
	 * 
	 * @param clazz
	 *            IPO 类,对应数据库的一个表。
	 * @return 查询到的结果，如果无结果，返回长度为0的数组。
	 * @throws PersistenceException
	 */
	public <T> List<T> findAll(Class<T> clazz) throws PersistenceException;

        /**
         * 返回查找的唯一值。
         * @param clazz
         * @param sqlWhere
         * @param params
         * @param sqlOrder
         * @return
         * @throws PersistenceException
         */
        public <T>  T findFirst(Class<T> clazz, String sqlWhere, List params,
			String sqlOrder) throws PersistenceException;

	/**
	 * 这个查询可以使用任何复杂的查询语句（有些持久化器可能不支持）， 由程序员负责语句的正确性和 参数的读取，item就是参数读取器，详见Item接口。
	 * Item接口是处理返回结果的钩子函数。
	 * 
	 * @param sql
	 *            Sql语句
	 * @param params
	 *            SQL语句中需要的参数
	 * @param item
	 *            结果处理器
	 * @return 处理结果列表
	 * @throws PersistenceException
	 */
	public List query(String sql, List params, final Item item)
			throws PersistenceException;

	/**
	 * 这个方法与query方法不同的是他通过主键查找相应的SQL，将所有的SQL语句编写到配置文件中。 这样处理便于数据库管理员优化SQL语句时使用。
	 * 
	 * @param sqlId
	 * @param sql
	 * @param params
	 * @return
	 * @throws PersistenceException
	 */
	public List queryById(String sqlId, List params, final Item item)
			throws PersistenceException;

	/**
	 * 这个方法可以使用任意负责的SQL语句，但是与query方法的本质不同是，他自动处理结果与类之间的映射关系。
	 * 找到类和查询结果中一一对应关系，将查询结果赋予属性值中。
	 * 
	 * @param clazz
	 *            查询结果的类
	 * @param sql
	 *            查询语句
	 * @param params
	 *            查询语句需要的参数
	 * @return 类的实例列表，如果查询结果的个数为0，则返回长度为0的列表。
	 * @throws PersistenceException
	 *             如果SQL语句有错误，或者其他SQL异常，则抛出此异常。
	 */
	public <T> List<T> query(Class<T> clazz, String sql, List params)
			throws PersistenceException;

//	/**
//	 * 这个方法可以对象所得到的赋值计算出查询条件，后面的参数是附加条件。 他根据对象的赋值自动拼装查询条件（只能处理相等查询）。
//	 * 他自动处理结果与类之间的映射关系。找到类和查询结果中一一对应关系，将查询结果赋予属性值中。
//	 * 
//	 * @param obj
//	 *            查询结果的类
//	 * @param lenient
//	 *            查询条件是严格匹配还是宽松匹配（like）
//	 * @param sql
//	 *            查询语句, 可以为空
//	 * @param params
//	 *            查询语句需要的参数， 可以为空
//	 * 
//	 * @return 类的实例列表，如果查询结果的个数为0，则返回长度为0的列表。
//	 * @throws PersistenceException
//	 *             如果SQL语句有错误，或者其他SQL异常，则抛出此异常。
//	 */
//	public List like(Object obj, boolean lenient, String sql, List params)
//			throws PersistenceException;

	/**
	 * 单值函数，根据一个返回条件返回一个单值，即查询结果的第一条记录的第一个值， 如果没有查询到任何记录，返回空。程序员要保证
	 * 
	 * @param sql
	 *            合法的SQL语句
	 * @param params
	 *            SQL语句中需要的参数
	 * @return 查询结果的第一条记录或者为空
	 * @throws PersistenceException
	 */
	public String queryString(String sql, List params)
			throws PersistenceException;

	/**
	 * 单值函数，根据一个返回条件返回一个单值，即查询结果的第一条记录的第一个值， 如果没有查询到任何记录，返回空。程序员要保证
	 * 
	 * @param sql
	 *            合法的SQL语句
	 * @param params
	 *            SQL语句中需要的参数
	 * @return 查询结果的第一条记录或者为空
	 * @throws PersistenceException
	 */
	public long queryLong(String sql, List params) throws PersistenceException;
	
	/**
	 * 取SQL语句第一行第一列的 Integer 类型返回值。如果没有检索到数据，则返回0； 如果检索的数据类型非长整类型，将抛出类型转换异常。
	 */
	public int queryInt(String sql, List params) throws PersistenceException;

	public long readSequence(String sequenceName) throws PersistenceException;


	/**
	 * 执行一个SQL语句
	 */
	public boolean execute(String sql, List params);
	
	/**
	 * 根据条件判断一个表中是否保护重复行
	 * @param tableName 表名称
	 * @param where     重复的条件
	 * @param params    条件的参数
	 * @return 如果有重复行，返回true，否则返回False
	 * @throws PersistenceException 如果SQL语句出现问题，则抛出此异常。
	 */
	public boolean duplicate (final Class po, final String where, final List params) throws PersistenceException;

	// public QueryResultList query(String sql, List params, final Item item,
	// int startIndex,
	// int endIndex)
	// throws PersistenceException;

	// /**
	// * 根据对象得到与其连接的对象，用于一对一连接。如果没有对于的连接情况，返回空。
	// */
	// public Object join(Class clazz, Object obj) throws PersistenceException;
	//
	// /**
	// * 根据对象得到与其连接的所有对象，用于一对多连接。如果没有对于的连接情况，返回空。
	// */
	// public List multiJoin(Class clazz, Object obj) throws
	// PersistenceException;

	/**
	 * 调用存储过程
	 * 
	 * @return boolean
	 */
	public boolean callProcedure(String name) throws PersistenceException;

	public Connection getConnection();
	
	public void close() throws PersistenceException;
}
