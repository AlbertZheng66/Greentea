package com.xt.proxy.utils;

import java.net.InetAddress;

/**
 * 服务定位接口，以获得服务器的位置信息。
 * @author zw
 *
 */

public interface ServerLocator {

	/**
	 * 返回服务器的地址信息
	 * @return
	 */
	public InetAddress getAddress();
	
	/**
	 * 返回服务器的端口信息
	 * @return
	 */
	public int getPort();

}
