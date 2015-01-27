package com.xt.proxy;

import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;

public interface Proxy {
	
	public Response invoke (Request request, Context context) throws Throwable;
        

}
