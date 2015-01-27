package com.xt.proxy.impl.http.hessian;

import org.apache.log4j.Logger;

import com.xt.core.log.LogWriter;
import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.local.LocalProxy2;
import com.xt.gt.sys.loader.ServerSystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;

public class HessianService implements Proxy {
	
	private final Logger logger = Logger.getLogger(Logger.class);
	
	static {
		SystemLoaderManager.getInstance().init(ServerSystemLoader.getInstance());
	}

	public Response invoke(Request request, Context context) throws Throwable {
		LogWriter.debug(logger, "request", request);
		// 安全审核！
		LocalProxy2 localProxy2 = new LocalProxy2();
		localProxy2.setSession(null);
		Response response = localProxy2.invoke(request, context);
		return response;
	}

}
