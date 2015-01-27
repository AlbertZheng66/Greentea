package com.xt.gt.jt.proc.result.ajax;

public class BooleanToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		Boolean ret = (Boolean)value;
		strBld.append(" type=\"boolean\" ");
		strBld.append(" value=\"").append(ret ? "1"	: "0").append("\"");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
	}

}
