package com.xt.proxy.event;

public class DefaultResponseProcessor implements ResponseProcessor {

	public Object process(Request req, Response res) {
		if (res == null) {
			return null;
		}
		if (req == null) {
			return res.getReturnValue();
		}
		return res.getReturnValue();
	}

}
