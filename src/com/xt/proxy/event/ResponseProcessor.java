package com.xt.proxy.event;

public interface ResponseProcessor {
	
    public Object process(Request req, Response res);
    
}
