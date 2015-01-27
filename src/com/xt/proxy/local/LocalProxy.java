package com.xt.proxy.local;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.db.pm.PersistenceManager;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.sys.RunMode;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.loader.SystemLoaderManager;

public class LocalProxy implements Proxy {

	// 在系统配置文件中定义使用的PersistenceManager
	private static SystemConfiguration sc = SystemConfiguration.getInstance();

	static {
		if (SystemLoaderManager.getInstance().getSystemLoader().getRunMode() != RunMode.SERVER) {

			InputStream is = LocalProxy.class
					.getResourceAsStream("/gt-config.xml");
			sc.load(is, true);
			// test
			Database.getInstance().load(
					(DatabaseContext) sc.readObject(
							SystemConstants.DATABASE_CONTEXT, null));
		}
	}

	private LocalProxy() {
	}

	public Response invoke(Request request, Context context) {
		if (request == null) {
			throw new SystemException("请求对象不能为空");
		}

		// 注入持久化管理器,用来控制事务
		PersistenceManager persistenceManager = null;

		try {
			Object serviceObject = ClassHelper
					.newInstance(request.getService());

			persistenceManager = new IPOPersistenceManager();
			DatabaseContext dc = (DatabaseContext) sc.readObject(
					SystemConstants.DATABASE_CONTEXT, null);
			persistenceManager.setDatabaseContext(dc);
			persistenceManager.setAutoCommit(false);

			// 注入过滤、排序和分页参数
			// FSPParameter fspParameter = request.getFSPParameter();
			// if (fspParameter != null) {
			// BeanHelper.copyProperty(service, "FSPParameter", fspParameter);
			//
			// persistenceManager.setFetchSize(fspParameter.getPagination()
			// .getRowsPerPage());
			//
			// persistenceManager.setStartIndex(fspParameter.getPagination()
			// .getStartIndex());
			// }

			inject(serviceObject, persistenceManager);

			Method method = ClassHelper.getMethod(serviceObject, request
					.getMethodName(), request.getParamTypes());

			/** ************ �?��执行业务处理方法 ******************** */
			Object ret = method.invoke(serviceObject, request.getParams());

			processCommit(persistenceManager);
			Response res = new Response();
			res.setReturnValue(ret);
			res.setServiceObject(serviceObject);
			// fspParameter = (FSPParameter) BeanHelper.getProperty(service,
			// "FSPParameter");
			// if (fspParameter != null) {
			// res.setFSPParameter(fspParameter);
			// }
			res.setRefParams(request.getParams());
			return res;
		} catch (IllegalAccessException e) {
			throw new SystemException("非法存取异常", e);
		} catch (InvocationTargetException e) {
			throw new SystemException("非法调用异常", e);
		} catch (SecurityException e) {
			throw new SystemException("业务处理方法不允许调用！", e);
		} catch (IllegalArgumentException e) {
			throw new SystemException("非法调用参数异常", e);
		} catch (Throwable e) {
			throw new SystemException("未知异常", e);
		} finally {
			processRollBack(persistenceManager);
		}
	}

	/**
	 * 注入持久化对象
	 * 
	 * @param sercie
	 * @param pm
	 * @return
	 */
	private Object inject(Object sercie, PersistenceManager pm) {

		DatabaseContext dc = (DatabaseContext) sc.readObject(
				SystemConstants.DATABASE_CONTEXT, null);
		pm.setDatabaseContext(dc);

		// 要检查业务类是否定义了持久化参数
		BeanHelper.copyProperty(sercie, "persistenceManager", pm);
		return pm;
	}

	/**
	 * 数据库回滚操�?
	 * 
	 * @param persistenceManager
	 */
	private void processRollBack(PersistenceManager persistenceManager) {
		if (persistenceManager != null) {
			persistenceManager.rollback();
		}
	}

	/**
	 * 数据库提交操�?
	 * 
	 * @param persistenceManager
	 */
	private void processCommit(PersistenceManager persistenceManager) {
		if (persistenceManager != null) {
			LogWriter.info("业务处理结束，事务将提交");
			persistenceManager.commit();
		}
	}

}
