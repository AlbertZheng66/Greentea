package com.xt.core.test.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BadParameterException;
import com.xt.gt.jt.event.RequestEvent;

public class MockRequestEvent implements RequestEvent {

	private Map attributes = new HashMap();

	private Map parameters = new HashMap();

	public Object getAttribute(String paramName) {
		return attributes.get(paramName);
	}

	public Object findAttribute(String paramName) {
		return attributes.get(paramName);
	}

	public Object getAttribute(String paramName, int scope) {
		return attributes.get(paramName);
	}

	public String getParameter(String paramName) {
		return (String) parameters.get(paramName);
	}

	public String[] getParameterValues(String paramName) {
		return (String[]) parameters.get(paramName);
	}

	public String[] getParameterNames() {
		return (String[]) parameters.keySet().toArray(new String[0]);
	}

	public int getInt(String paramName) {
		return Integer.parseInt(getAttribute(paramName).toString());
	}

	public double getDouble(String paramName) {
		return Double.parseDouble(getAttribute(paramName).toString());
	}

	public long getLong(String paramName) {
		return Long.parseLong(getAttribute(paramName).toString());
	}

	public float getFloat(String paramName) {
		return Float.parseFloat(getAttribute(paramName).toString());
	}

	public InputStream getInputStream(String name) {
		return null;
	}

	public Object[] getObjects(String collName, Class clazz)
			throws BadParameterException {
		return null;
	}

	public HttpServletRequest getRequest() {
		return new MockHttpServletRequest();
	}

	public void putAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public void putParameter(String name, String value) {
        parameters.put(name, value);
	}
	
	public void clear () {
		this.attributes.clear();
		this.parameters.clear();
	}
}
