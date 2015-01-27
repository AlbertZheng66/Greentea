package com.xt.proxy.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.xt.core.exception.SystemException;

public class LocalServerLocator implements ServerLocator{

	public InetAddress getAddress() {
		
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new SystemException("未能找到本地地址信息！", e);
		}
	}

	public int getPort() {
		return 8080;
	}

	

}
