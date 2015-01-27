package com.xt.gt.sys;

/**
 * 当前 JVM 是以何种方式运行的。运行模式将影响到参数、字典、资源等读取方式。
 *  如果参数未设置（默认），表示以“独立应用客户端模式运行”。
 * @author albert
 *
 */
public enum RunMode {

	/**
	 * 服务器模式，应用在服务器端运行。
	 */
	SERVER, 
	
	/**
	 * 离线模式下运行。
	 */
	OFF_LINE,
	
	/**
	 * Web Start 客户端模式，应用采用 Web Start 模式启动。
	 */
	WEB_START_CLIENT, 
	
	/**
	 * 独立应用客户端模式，和 CLIENT_SERVER 模式的区别是其可采用远程协议（HTTP Strean、RMI等）访问
	 * 服务器。
	 */
	APP_CLIENT, 
	
	/**
	 * Applet 客户端模式，客户端是一个 Applet 页面。
	 */
	APPLET_CLIENT, 
	
	/**
	 * 测试模式，用于测试数据库，配置情况基本与 CLIENT_SERVER 相同。
	 */
    TEST,
    
    /**
     * C/S 模式，采用数据库直连的方式，访问网内的数据库。
     */
    CLIENT_SERVER,

    /**
     * 分布式的运行（但不是通过资源管理中心进行控制）
     */
    CLUSTER,

    /**
     * 通过数据中心统一控制的分布式模式。这种情况下，某些资源和参数的获取需要通过
     * RAManager（资源和应用管理器）来获取。
     */
    DATA_CENTER_CLUSTER,
}
