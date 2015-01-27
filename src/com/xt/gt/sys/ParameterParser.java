package com.xt.gt.sys;

public interface ParameterParser {
	
	public String getParameterName ();
	
	public Object parse(SystemParameter systemParameter);

}
