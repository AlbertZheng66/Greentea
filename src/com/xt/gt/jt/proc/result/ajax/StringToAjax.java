package com.xt.gt.jt.proc.result.ajax;

public class StringToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"String\" ");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		strBld.append(value.toString());
	}

}
