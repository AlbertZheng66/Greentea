package com.xt.gt.jt.proc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.db.pm.PersistenceManager;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.BaseException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.message.Messages;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.BizHandlerInfoFactory;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.bh.callback.RequestProcessor;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.jt.proc.inject.PersistenceInjector;
import com.xt.gt.jt.proc.inject.RequestInjector;
import com.xt.gt.jt.proc.inject.ResponseEventInjector;
import com.xt.gt.jt.proc.pre.PrepareParams;
import com.xt.gt.jt.proc.pre.PrepareParamsFactory;
import com.xt.gt.jt.proc.result.CollectionResultProcessor;
import com.xt.gt.jt.proc.result.FileDownloadResultProcessor;
import com.xt.gt.jt.proc.result.InputStreamResultProcessor;
import com.xt.gt.jt.proc.result.MapResultProcessor;
import com.xt.gt.jt.proc.result.ObjectResultProcessor;
import com.xt.gt.jt.proc.result.ResponseEventResultProcessor;
import com.xt.gt.jt.proc.result.ResultProcessor;
import com.xt.gt.jt.proc.result.VoidResultProcessor;
import com.xt.gt.jt.proc.result.ajax.AjaxResultProcessor;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.views.taglib.html.MessagesTag;

/**
 * HTTP请求处理器。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:HTTP请求处理负责处理浏览器（也可以是别的客户端）发出的HTTP请求。 处理的过程如下：
 * <li> 类（放在静态方法中）的初始化</li>
 * <li> 根据映射关系得到业务处理类（服务类）和业务处理方法， 转换过程可自定义或者采用框架的过程。</li>
 * <li> 注入属性，包括注入持久化属性</li>
 * <li> 注入参数，即将HTTP请求中的参数映射到业务处理方法的参数中</li>
 * <li> 默认处理翻页参数</li>
 * <li> 调用业务处理方法</li>
 * <li> 根据是否抛出处理事务</li>
 * <li> 根据返回结果存放到指定的位置</li>
 * <li> </li>
 * </p>
 * <p>
 * 
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-7-27
 */
public class HttpRequestProcessor implements RequestProcessor {

	/**
	 * 系统参数，不能作为某个服务的输入参数
	 */
	private static Set<String> systemParams = new HashSet<String>();

	// 在系统配置文件中定义使用的PersistenceManager
	static SystemConfiguration sc = SystemConfiguration.getInstance();

	// 结果处理器列表
	private static List<Class> resultProcessors = new ArrayList<Class>();

	// 属性注入器列表
	private static List<RequestInjector> injectorProcessors = new ArrayList<RequestInjector>();

	// 排序处理器
	private static Class sortProcessor;

	// 翻页处理器
	private static Class pagenitionProcessor;

	static {
		// 不能反射的参数
		systemParams.add("bizHandler");
		systemParams.add("method");

		// 增加结果处理类,用户自定义的处理类型如果和系统定义的类型相同，则系统默认的类型将不
		// 起作用！
		String[] rps = sc.readStrings(SystemConstants.RESULT_PROCESSORS);
		if (rps != null) {
			for (int i = 0; i < rps.length; i++) {
				resultProcessors.add(ClassHelper.getClass(rps[i]));
			}
		}

		/************* 系统默认的结果处理器  *******************/
		//处理AJAX请求
		resultProcessors.add(AjaxResultProcessor.class);
		
		// 处理普通的HTTP请求
		resultProcessors.add(FileDownloadResultProcessor.class);
		resultProcessors.add(InputStreamResultProcessor.class);
		resultProcessors.add(MapResultProcessor.class);
		resultProcessors.add(ResponseEventResultProcessor.class);
		resultProcessors.add(CollectionResultProcessor.class);
		resultProcessors.add(ObjectResultProcessor.class);
		resultProcessors.add(VoidResultProcessor.class);

		// 属性注入器列表
		injectorProcessors.add(new PersistenceInjector());
		injectorProcessors.add(new ResponseEventInjector());

		// 排序处理程序
		String sortClassName = sc.readString(SystemConstants.SORT_PROCESSOR);
		if (sortClassName == null) {
			sortProcessor = DefaultSortProcessor.class;
		} else {
			sortProcessor = ClassHelper.getClass(sortClassName);
		}

		// 分页处理程序
		String pageClassName = sc
				.readString(SystemConstants.PAGINATION_PROCESSOR);
		if (pageClassName == null) {
			pagenitionProcessor = DefaultPaginationProcessor.class;
		} else {
			pagenitionProcessor = ClassHelper.getClass(pageClassName);
		}

	}

	/**
	 * 
	 * 构建函数
	 */
	public HttpRequestProcessor() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xt.gt.chb.proc.HttpRequestCallBack#process(com.xt.gt.chb.event.RequestEvent,
	 *      javax.servlet.http.HttpServletResponse,
	 *      com.xt.gt.chb.proc.ActionParameter)
	 */
	public Object process(RequestEvent requestEvent, HttpServletResponse res,
			ActionParameter actionParameter) throws BaseException {
		LogWriter.info("RequestProcessor: process................");
		// 业务处理方法
		Object bizHandler = null;

		/** ******** 开始读取业务处理类及其方法 ***************** */
		// 得到用户的类和方法()
		String bizHandlerName = actionParameter.getBizHandler();

		LogWriter.debug("bizHandlerName", bizHandlerName);

		// 校验业务处理类的名称是否合法
		if (null == bizHandlerName || 0 == bizHandlerName.trim().length()) {
			throw new BadParameterException("未定义业务处理方法名称！");
		}

		BizHandlerInfo bhInfo = getBizHandler(actionParameter.getAllPackages(),
				bizHandlerName);

		if (bhInfo == null) {
			throw new BadParameterException("未发现相应的业务处理方法！");
		}
		LogWriter.debug("bhInfo", bhInfo.getName());
		LogWriter.debug("bhInfo.getClazz()", bhInfo.getHandlerClass());
		
		String methodName = actionParameter.getMethod();
		// 校验业务处理方法的名称是否合法
		if (null == methodName || 0 == methodName.trim().length()) {
			throw new BadParameterException("未定义业务处理方法名称！");
		}
		DealMethodInfo dmInfo = BizHandlerInfoFactory.getInstance()
				.getDealMethodInfo(bhInfo, methodName);

		// 如果用户自定义了用户名，则使用用户自定义的用户名
		methodName = dmInfo.getMethod();
		LogWriter.debug("业务处理方法名称（客户端）", dmInfo.getName());
		LogWriter.debug("业务处理方法", dmInfo.getMethod());
		LogWriter.debug("业务处理方法参数类型", dmInfo.getParamClazz());
		/** ******** 开始读取业务处理类及其方法 ***************** */

		// 持久化管理器,用来控制事务
		PersistenceManager persistenceManager = null;

		try {
			// 实例化的类，并得到方法参数的类型
			bizHandler = ClassHelper.newInstance(bhInfo.getHandlerClass());

			LogWriter.debug("/** ********** 开始注入属性 ***************** */");
			for (Iterator iter = injectorProcessors.iterator(); iter.hasNext();) {
				RequestInjector injector = (RequestInjector) iter.next();
				if (injector.needInject(bhInfo, dmInfo)) {
					// 注入属性并得到注入的返回值
					Object ret = injector.inject(bizHandler);

					// 得到持久化管理器，以便进行事务管理
					if (ret != null && ret instanceof PersistenceManager) {
						persistenceManager = (PersistenceManager) ret;
						persistenceManager.setAutoCommit(false);
					}
				}
			}
			/** ********** 开始注入属性 ***************** */

			LogWriter.debug("/** 开始处理排序和翻页参数 */");
			if (persistenceManager != null) {
				PaginationProcessor pg = (PaginationProcessor) ClassHelper
						.newInstance(pagenitionProcessor);
				pg.processTurnPage(persistenceManager, requestEvent);
                //FIXME: 这样处理排序有问题
//				SortProcessor sp = (SortProcessor) ClassHelper
//						.newInstance(sortProcessor);
//				sp.processSort(persistenceManager, requestEvent);
			}
			/** ********** 开始注入翻页参数 ***************** */

			LogWriter.debug("/** ******** 开始准备业务处理方法的参数  ******* */");
			PrepareParams prepareParams = PrepareParamsFactory.getInstance()
					.getPrepareParams(dmInfo);

			// 得到处理方法的输入值
			Object[] params = prepareParams.getParams(bizHandler, dmInfo,
					requestEvent);

			// 得到处理方法的参数类型
			Class[] paramClasses = prepareParams.getParamClasses(bizHandler,
					dmInfo, requestEvent);

			/** ************ 准备业务处理方法的参数结束 **************** */

			/** ************ 开始执行业务处理方法 ******************** */
			Object ret = invokeBizHandler(bhInfo, methodName, bizHandler,
					paramClasses, params, requestEvent);
			/** ************ 执行业务处理方法结束 ******************** */

			// 处理返回结果，并将其放到Model中
			processResponse(ret, requestEvent.getRequest(), res, bizHandler,
					actionParameter, params);

			// 业务处理没有问题则进行提交操作
			processCommit(persistenceManager);
			
			// 销毁创建的对象
			bizHandler = null;
			params = null;
			
			return ret;
		} finally {
			// 无论成功与失败都回滚一次数据库
			processRollBack(persistenceManager);
			persistenceManager.close();
			persistenceManager = null;
		}
	}
	
	/**
	 * 
	 *
	 */
	private void destroy () {
		
	}

	/**
	 * 调用业务处理方法，只所以要单独写一个方法，是要将其异常统一处理。
	 * 
	 * @param bhInfo
	 * @param methodName
	 *            业务处理方法
	 * @param bizHandler
	 *            业务处理类实例
	 * @param params
	 *            业务处理方法的输入参数
	 * @param paramClasses
	 *            业务处理方法的输入类型
	 * @return
	 * @throws SystemException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object invokeBizHandler(BizHandlerInfo bhInfo, String methodName,
			Object bizHandler, Class[] paramClasses, Object[] params,
			RequestEvent requestEvent) {
		LogWriter.debug("调用业务处理方法", methodName);

		// 业务逻辑方法
		Method bizMethod = null;

		Object ret = null;
		try {
			LogWriter.debug("paramClasses", paramClasses);

			bizMethod = bhInfo.getHandlerClass().getDeclaredMethod(methodName,
					paramClasses);
			LogWriter.debug("params", params);
			LogWriter.debug("bizMethod", bizMethod);

			if (null == bizMethod) {
				throw new SystemException("未发现合适的调用方法");
			}

			ret = bizMethod.invoke(bizHandler, params);

			// 处理返回的消息
			processMessages(bizHandler, requestEvent);
		} catch (BaseException e) {
			throw e;  //系统异常不要捕获
		} catch (IllegalAccessException e) {
			throw new SystemException("非法存取异常！", e);
		} catch (InvocationTargetException e) {
			throw new SystemException("非法调用异常！", e);
		} catch (SecurityException e) {
			throw new SystemException("业务处理方法不允许调用！");
		} catch (NoSuchMethodException e) {
			throw new SystemException("业务处理方法不存在！");
		} catch (IllegalArgumentException e) {
			throw new SystemException("非法调用参数异常！", e);
		}
		LogWriter.debug("after method invoke.....");
		return ret;
	}

	/**
	 * 数据库回滚操作
	 * 
	 * @param persistenceManager
	 */
	private void processRollBack(PersistenceManager persistenceManager) {
		if (persistenceManager != null) {
			persistenceManager.rollback();
		}
	}

	/**
	 * 数据库提交操作
	 * 
	 * @param persistenceManager
	 */
	private void processCommit(PersistenceManager persistenceManager) {
		if (persistenceManager != null) {
			LogWriter.info("业务处理结束，事务将提交！");
			persistenceManager.commit();
		}
	}

	private BizHandlerInfo getBizHandler(String packageName, String bizHandler) {
		return BizHandlerInfoFactory.getInstance().getBizHandlerInfo(
				packageName, bizHandler);
	}

	/**
	 * 处理业务处理的返回值，将其按照规则存放在request或者session的属性中。 1.
	 * 如果返回的值是ResponseEvent类的实例，则一次处理用户存放的对象； 2.
	 * 如果对象是存放在Map中的对象，则将对象取出，按照属性和值，存放在request的属性中； 3.
	 * 如果对象是ISession的一个实现类，则将对象的属性和名称提前处理，存放在session的属性中； 4.
	 * 如果对象是Object的子类，将对象的属性和名称提取处理，存放在request的属性中； 5.
	 * 如果对象是基本类型，系统不予处理，也不提示错误信息。
	 * 
	 * @param ret
	 * @param res
	 */
	private void processResponse(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) {
		for (Iterator iter = resultProcessors.iterator(); iter.hasNext();) {
			ResultProcessor processor = (ResultProcessor) ClassHelper
					.newInstance((Class) iter.next());
			// 如果有一个处理器处理了结果，便不再继续处理。
			if (processor.willProcess(ret, req)) {
				LogWriter.info("result processor.class", processor.getClass()
						.getName());
				processor.process(ret, req, res, bizHandler, processParams,
						params);
				break;
			}
		}
	}

	/**
	 * 处理返回消息。如果业务类定义了“getMessages”，且其方法参数为空，返回值为Messages
	 * 类型，将将其返回的消息存放到Request的属性中。
	 * 
	 * @param Object
	 *            业务处理类
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void processMessages(Object bizHandler, RequestEvent requestEvent)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method messagesGetter = ClassHelper.getMethod(bizHandler.getClass(),
				"getMessages", null);
		if (messagesGetter != null
				&& Messages.class == messagesGetter.getReturnType()) {
			Messages messages = (Messages) messagesGetter.invoke(bizHandler,
					new Object[0]);
			if (null != messages) {
				requestEvent.getRequest().setAttribute(
						MessagesTag.MESSAGES_IN_REQUEST, messages);
			}
		}
	}
}
