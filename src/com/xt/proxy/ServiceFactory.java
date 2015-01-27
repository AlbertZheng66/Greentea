package com.xt.proxy;

import net.sf.cglib.proxy.Enhancer;

import com.xt.core.log.LogWriter;
import com.xt.core.service.IService;

/**
 * 用于产生远程服务接口的工厂类。此工厂类首先将服务类转换为相应的服务代理类。 这个代理将负责调用真正的远程的服务方法。具体采用的协议，由
 * ProxyFactory 类 负责处理。 本类的操作过程有两步，首先对类进行改写，生成一个服务类的子类，并覆盖掉其公共方法； 注意：只改写公用的非
 * final 的方法。然后调用服务的代理方法。
 * 
 * @author albert
 * 
 */
public class ServiceFactory {

	private static ServiceFactory instance;

	static {
		instance = new ServiceFactory();
	}

	private ServiceFactory() {

	}

	static public ServiceFactory getInstance() {
		return instance;
	}

	synchronized public <T extends IService> T getService(Class<T> serviceClass) {
		// 从上下文中得到随附参数变量
		T service = createSerivce(serviceClass, null, false, null);
		return service;
	}

    /**
     * 通过制定代理的方式返回一个类的代理类。
     * @param <T>
     * @param serviceClass
     * @param proxy
     * @return
     */
    synchronized public <T extends IService> T getService(Class<T> serviceClass, Proxy proxy) {
		// 从上下文中得到随附参数变量
		T service = createSerivce(serviceClass, proxy, false, null);
		return service;
	}

	/**
	 * 
//	 * @param <T>
//	 * @param serviceClass
//	 * @param hiddenParams
//	 *            隐含参数，用于传递分页、过滤、排序等信息
//	 * @return
//	 */
//	synchronized public <T extends IService> T getService(
//			Class<T> serviceClass, FSPParameter fspParameter) {
//		if (serviceClass == null) {
//			throw new SystemException("服务类不能为空。");
//		}
//		// Class<T> newClass = factory.modify(serviceClass);
//		// if (modifiedClasses.containsKey(serviceClass)) {
//		// newClass = modifiedClasses.get(serviceClass);
//		// } else {
//		//
//		// // 如果当前运行模式是以 Web Start 或者 Applet 方式运行的话，考虑到安全等问题，
//		// // 则需要到服务器端加载类。
//		// //
//		// LogWriter.debug("systemLoader.getRunMode()=",
//		// systemLoader.getRunMode());
//		// if (systemLoader.getRunMode() == RunMode.APPLET_CLIENT
//		// || systemLoader.getRunMode() == RunMode.WEB_START_CLIENT) {
//		// // 查看一下当前线程的类加载器，并将其设置为指定的类加载器。
//		// if (contextClassLoader != Thread.currentThread()
//		// .getContextClassLoader()) {
//		// Thread.currentThread().setContextClassLoader(
//		// contextClassLoader);
//		// }
//		// String newClassName = factory.getNewName(serviceClass);
//		// LogWriter.debug("newClassName=", newClassName);
//		// String oldClassName = serviceClass.getName();
//		// contextClassLoader.register(newClassName, oldClassName);
//		// newClass = ClassHelper.getClass(newClassName);
//		// } else {
//		// newClass = factory.modify(serviceClass);
//		// }
//		// modifiedClasses.put(serviceClass, newClass);
//		// }
//		// T service = ClassHelper.newInstance(newClass);
//		// if (fspParameter != null) {
//		// BeanHelper.copyProperty(service,
//		// ServiceStubModifier.FSP_PARAMETER_NAME, fspParameter);
//		//
//		// }
//		T service = createSerivce(serviceClass, fspParameter, false, null);
//		return service;
//	}

	private <T extends IService> T createSerivce(Class<T> serviceClass, Proxy proxy,
			boolean asynchInvoke, Task task) {
		T service = createObjectWithCglib(serviceClass, proxy, asynchInvoke, task);
		return service;
	}

	@SuppressWarnings("unchecked")
	private <T extends IService> T createObjectWithCglib(Class<T> serviceClass, Proxy proxy, boolean asynchInvoke, Task task) {
		Enhancer enhancer = new Enhancer();
		LogWriter.debug("createObjectWithCglib...........", serviceClass);
		enhancer.setSuperclass(serviceClass);
		ServiceMethodInterceptor<T> smi = new ServiceMethodInterceptor<T>(serviceClass, proxy);
		smi.setAsynchInvoke(asynchInvoke);
		smi.setTask(task);
		enhancer.setCallback(smi);
		return (T) enhancer.create();
	}

//	public <T extends IService> T getAsynchService(Class<T> serviceClass,
//			FSPParameter fspParameter, Task task) {
//		T service = createSerivce(serviceClass, fspParameter, true, task);
//		return service;
//	}

	synchronized public <T extends IService> T getAsynchService(Class<T> serviceClass,
			Task task) {
		T service = createSerivce(serviceClass, null, true, task);
		return service;
	}

    synchronized public <T extends IService> T getAsynchService(Class<T> serviceClass, Proxy proxy,
			Task task) {
		T service = createSerivce(serviceClass, proxy, true, task);
		return service;
	}
}
 