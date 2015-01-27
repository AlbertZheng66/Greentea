package com.xt.proxy.impl.ejb;

import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;

public class EjbProxy implements Proxy {

	public Response invoke(Request request, Context client) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
