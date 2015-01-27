package com.xt.proxy;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.utils.ClassHelper;
import com.xt.proxy.impl.http.hessian.HessianClientProxy;
import com.xt.proxy.impl.http.stream.HttpStreamProxy;
import com.xt.proxy.local.LocalProxy2;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;

public class ProxyFactory {

	private static Logger logger = Logger.getLogger(ProxyFactory.class);

	private static ProxyFactory instance;

	/**
	 * 代理的类型：目前支持：XMLoHTTP(默认方式)，EJB，RMI，LOCAL（不必部署容器，在本地进行测试）。
	 */
	public String proxyType = DEFAULT_PROXY_TYPE;

	/**
	 * 采用HTTP Stream这种通信方式
	 */
	public static final String PROXY_TYPE_HTTP_STREAM = "httpStream";

	/**
	 * 采用EJB这种通信方式
	 */
	public static final String PROXY_TYPE_EJB = "ejb";

	/**
	 * 采用RMI这种通信方式
	 */
	public static final String PROXY_TYPE_RMI = "rmi";

	/**
	 * 采用LOCAL这种通信方式
	 */
	public static final String PROXY_TYPE_LOCAL = "local";
	

	/**
	 * 采用 HESSIAN 这种通信方式
	 */
	public static final String PROXY_TYPE_HESSIAN = "hessian";
	

	/**
	 * 默认的代理类型，本地代理。
	 */
	public static final String DEFAULT_PROXY_TYPE = PROXY_TYPE_LOCAL;

	/**
	 * 代理名称及其对应处理方式的映射表
	 */
	private static Map<String, Class<? extends Proxy>> proxyMap = new HashMap<String, Class<? extends Proxy>>();

	static {
		proxyMap.put(PROXY_TYPE_HTTP_STREAM, HttpStreamProxy.class);
		proxyMap.put(PROXY_TYPE_LOCAL,       LocalProxy2.class);
		proxyMap.put(PROXY_TYPE_HESSIAN,     HessianClientProxy.class);
		// proxyMap.put(PROXY_TYPE_EJB, new HttpXmlProxy());
		// proxyMap.put(PROXY_TYPE_RMI, new HttpXmlProxy());
	}

	private ProxyFactory() {
	}

	public static ProxyFactory getInstance() {
		if (instance == null) {
			instance = new ProxyFactory();
			// 首先读取系统参数
			SystemLoader loader = SystemLoaderManager.getInstance()
					.getSystemLoader();
			if (loader == null) {
				throw new SystemException("不能确定系统加载器。");
			}
			instance.proxyType = loader.getProxyType();
			logger.info("system property proxyType=" + instance.proxyType);

		}
		return instance;
	}

	/**
	 * 根据代理类型返回相应的代理类实例，如果类型尚未得到支持或者不存在，则抛出异常。
	 * 
	 * @return
	 */
	synchronized public Proxy getDefaultProxy() {
		return getProxy(proxyType);
	}
	
	/**
	 * 根据代理类型返回相应的代理类实例，如果类型尚未得到支持或者不存在，则抛出异常。
	 * 
	 * @return
	 */
	synchronized public Proxy getProxy(String proxyType) {
		if (StringUtils.isEmpty(proxyType) || !proxyMap.containsKey(proxyType)) {
			throw new SystemException(String.format("代理类型为空[%s]或者未找到合适的代理类。", proxyType));
		}
		Class<? extends Proxy> proxyClass = proxyMap.get(proxyType);
		Proxy proxy = ClassHelper.newInstance(proxyClass);
		return proxy;
	}

	public String getProxyType() {
		return proxyType;
	}

	public void setProxyType(String proxyType) {
		this.proxyType = proxyType;
	}

}
