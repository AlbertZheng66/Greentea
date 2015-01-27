package com.xt.core.db.po;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;

//import com.sun.rowset.CachedRowSetImpl;
import com.xt.core.db.pm.Item;
import com.xt.core.exception.POException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.PreparedStatementHelper;
import com.xt.core.utils.SqlUtils;


/**
 * 查询工具类。
 * <p>Title: XT框架-持久化部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-16
 */
public class QueryUtils {
	
	private static Logger logger = Logger.getLogger(QueryUtils.class);
	
	/**
	 * 构造函数
	 */
	public QueryUtils() {
	}

	/**
	 * 查询方法。查询满足strSql条件的数据项，并将查询结果封装到ResultSetItem中（ResultSetItem由使用者
	 * 自定义数据存放格式）。该方法会将捕获到的异常封装成POException异常抛出。
	 * 
	 * @param con -
	 *            与数据库的连接
	 * @param strSql -
	 *            将要执行的SQL查询语句；
	 * @param sqlParams －
	 *            List 类型的属性，该属性
	 * @param rsItem
	 *            用户使用此接口转换结果集的形式 用来存放将要执行的SQL语句中的参数（即，所有“？”的值）。sqlParams中封装了所有的
	 *            参数，他们按照在SQL语句中出现的先后顺序被封装在List中。
	 * @return List - 得到的结果数组列表
	 * @throws POException
	 */
	public static List findBySQL(Connection con, String strSql, List sqlParams,
			ResultSetItem rsItem) throws POException {
		LogWriter.info(logger, "对数据库进行查询操作，SQL:", strSql); // 记录日志
		LogWriter.info(logger, "sqlParams == ", sqlParams == null ? "[]" : sqlParams.toString());
		List ret = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			LogWriter.debug(logger, "strSql == " + strSql);
			ps = preparedStatementCreate(con, strSql, sqlParams); // 生成preparedStatement
																	// 实例
			rs = ps.executeQuery(); // 执行查询操作
			while (rs.next()) {
				ret.add(rsItem.createObject(rs));
			}
			return ret;
		} catch (SQLException ex) {
			throw new POException("数据库异常。", ex);
		} finally {
			SqlUtils.close(ps, rs);
		}
	}

	/**
	 * 根据给定的SQL进行查询。查询的结果由ResultSetItem处理并显示成定义的形式，返回的记录位置从startIndex
	 * 开始，最多显示的条数由maxItems决定。
	 * 
	 * @param con
	 *            数据库的连接
	 * @param sql
	 *            查询语句
	 * @param sqlParams
	 *            查询语句的参数
	 * @param rsItem
	 *            用户使用此接口转换结果集的形式
	 * @param startIndex
	 *            结果集的开始位置
	 * @param maxItems
	 *            最多返回的查询结果的数量
	 * @return 数组列表
	 * @throws POException
	 */
	public static QueryResultList findBySQL(Connection con, String sql,
			List sqlParams, final ResultSetItem rsItem, int startIndex,
			int maxItems) throws POException {
		LogWriter.info(logger, "对数据库进行查询操作，SQL:", sql); // 记录日志
		LogWriter.info(logger, "sqlParams == ", sqlParams == null ? "[]" : sqlParams.toString());
		final QueryResultList ret = new QueryResultList();
		ret.setStartIndex(startIndex);
		// 使用匿名类处理结果的表现形式
		long totalItems = query(con, sql, sqlParams, startIndex, maxItems,
				new ProcessResultSet() {
					public void dealWith(ResultSet rs) throws SQLException {
						ret.addItem(rsItem.createObject(rs));
					}
				});
		ret.setTotalItems(totalItems);
		return ret;
	}
	
	/**
	 * 根据给定的SQL进行查询。查询的结果由ResultSetItem处理并显示成定义的形式，返回的记录位置从startIndex
	 * 开始，最多显示的条数由maxItems决定。
	 * 
	 * @param con
	 *            数据库的连接
	 * @param sql
	 *            查询语句
	 * @param sqlParams
	 *            查询语句的参数
	 * @param rsItem
	 *            用户使用此接口转换结果集的形式
	 * @param startIndex
	 *            结果集的开始位置
	 * @param maxItems
	 *            最多返回的查询结果的数量
	 * @return 数组列表
	 * @throws POException
	 */
	public static QueryResultList findBySQL(Connection con, String sql,
			List sqlParams, final Item item, int startIndex,
			int maxItems) throws POException {
		LogWriter.info(logger, "对数据库进行查询操作，SQL:" + sql); // 记录日志
		LogWriter.info(logger, "sqlParams == " + sqlParams);
		final QueryResultList ret = new QueryResultList();
		ret.setStartIndex(startIndex);
		// 使用匿名类处理结果的表现形式
		long totalItems = query(con, sql, sqlParams, startIndex, maxItems,
				new ProcessResultSet() {
					public void dealWith(ResultSet rs) throws SQLException {
                                            Object obj = item.createObject(rs);
                                            if (obj != null) {
						ret.addItem(obj);
                                            }
					}
				});
		ret.setTotalItems(totalItems);
		return ret;
	}

	public static QueryResultList findBySQL(Connection con, String sql,
			List sqlParams, final IPO ipo, int startIndex, int maxItems)
			throws POException {
		LogWriter.info(logger, "对数据库进行查询操作，sql:", sql); // 记录日志
		LogWriter.info(logger, "sqlParams == ", sqlParams == null ? "[]" : sqlParams.toString());
		final QueryResultList ret = new QueryResultList();
		ret.setStartIndex(startIndex);
		
		final Class clazz = ipo.getClass();
        
		// 使用匿名类处理结果的表现形式
		long totalItems = query(con, sql, sqlParams, startIndex, maxItems,
				new ProcessResultSet() {
	    	        private IPO queryRes = (IPO)ClassHelper.newInstance(clazz);
					public void dealWith(ResultSet rs) throws SQLException {
//						初始化对象，并装入结果列表
						Object obj = ClassHelper.newInstance(ipo.__getEntityClass());
				    	queryRes.__load(rs, obj);
				    	ret.addItem(queryRes);
					}
				});
		ret.setTotalItems(totalItems);
		return ret;
	}

	
	/**
	 * 生成可以上下滚动，只读的PreparedStatement实例，然后为PreparedStatement<br>
	 * 实例添加属性（data中传递进来的）。最后返回PreparedStatement<br>
	 * 实例。PreparedStatement由参数con生成。PreparedStatement<br>
	 * 中需要设置的参数由sqlParam给出。方法可以根据参数类型将参数转换<br>
	 * 为该类型，并调用PreparedStatement中相应的setＸＸＸ方法设置参<br>
	 * 数。
	 * 
	 * @param con
	 *            与数据库建立的连接
	 * @param sqlStr
	 *            将要执行的sql语句
	 * @param sqlParams
	 *            SQL语句中的参数（“?”）
	 * @return PreparedStatement 生成的PreparedStatement实例
	 * @throws SQLException
	 */
	protected static PreparedStatement preparedStatementCreate(Connection con,
			String sqlStr, List sqlParams) throws SQLException {
		// 生成preparedStatement实例
		PreparedStatement ps = con.prepareStatement(sqlStr,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		if (sqlParams == null) {
			return ps;
		}
		// 指示SQL查询语句中参数的位置
		int index = 1;
		for (Iterator iter = sqlParams.iterator(); iter.hasNext();) {
			// 判断各个参数的类型，并调用相应的setter方法；
			Object value = iter.next();
			PreparedStatementHelper.setValue(ps, index++, value);
		}
		return ps;
	}

//	/**
//	 * 查询方法。查询满足strSql条件的数据项，然后通过dealWith方法形成结果集。
//	 *
//	 * @param con -
//	 *            与数据库的连接
//	 * @param sqlStr -
//	 *            将要执行的SQL查询语句；
//	 * @param sqlParams －
//	 *            List 类型的属性，该属性 用来存放将要执行的SQL语句中的参数（即，所有“?”的值）。
//     * sqlParams中封装了所有的
//	 *            参数，他们按照在SQL语句中出现的先后顺序被封装在List中。
//	 * @param prs -
//	 *            用户使用此接口转换结果集的形式
//	 * @throws POException
//	 */
//	private static void query(Connection con, String sqlStr, List sqlParams,
//			ProcessResultSet prs) throws POException {
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			LogWriter.debug(logger, "QueryUtils query sql==" + sqlStr);
//			ps = preparedStatementCreate(con, sqlStr, sqlParams); // 生成preparedStatement
//																	// 实例
//			rs = ps.executeQuery(); // 执行查询操作
//			while (rs.next()) {
//				// 形成结果集
//				prs.dealWith(rs);
//			}
//		} catch (SQLException ex) {
//			throw new POException("数据库异常。", ex);
//		} finally {
//			SqlUtils.close(ps, rs);
//		}
//	}

	/**
	 * 查询方法。查询满足strSql条件的数据项，然后通过dealWith方法形成结果集。
	 * 
	 * @param con -
	 *            与数据库的连接
	 * @param sqlStr -
	 *            将要执行的SQL查询语句；
	 * @param sqlParams －
	 *            List 类型的属性，该属性 用来存放将要执行的SQL语句中的参数（即，所有“？”的值）。sqlParams中封装了所有的
	 *            参数，他们按照在SQL语句中出现的先后顺序被封装在List中。
	 * @param prs -
	 *            用户使用此接口转换结果集的形式
	 * @param startIndex
	 *            结果集的开始位置
	 * @param maxItems
	 *            最多返回的查询结果的数量
	 * @throws POException
	 */
	private static long query(Connection con, String sqlStr, List sqlParams,
			int startIndex, int maxItems, ProcessResultSet prs)
			throws POException {
		int totalItems = 0; // 查询结果的总数
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			LogWriter.debug(logger, "strSql == findBySQL", sqlStr);
			ps = preparedStatementCreate(con, sqlStr, sqlParams); // 生成preparedStatement
																	// 实例
			rs = ps.executeQuery(); // 执行查询操作
			// 停止位置
			int endIndex = (maxItems <= 0 ? Integer.MAX_VALUE : startIndex + maxItems); 
			LogWriter.debug(logger, "QueryUtils startIndex == ", startIndex);
			LogWriter.debug(logger, "QueryUtils endIndex == ", endIndex);
			while (rs.next()) {
				// 确定起始的位置
				if (rs.getRow() - 1 < startIndex) {  // 注意：getRow()是从 1 开始计数，而 startIndex 是从0开始计数
					continue;
				}
				
				// 确定终止位置
				if (rs.getRow() > endIndex) {
					break;
				}
				// 形成结果集
				prs.dealWith(rs);
			}
		} catch (SQLException ex) {
			throw new POException("数据库异常。", ex);
		} finally {
			SqlUtils.close(ps, rs);
		}
		return totalItems;
	}
}

/**
 * 处理结果集的接口，query方法处理查询时使用此接口
 */
interface ProcessResultSet {
	public void dealWith(ResultSet rs) throws SQLException;

}
