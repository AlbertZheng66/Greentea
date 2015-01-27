package com.xt.gt.jt.proc.result.ajax;


public class NullToAjax  implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"null\" ");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		
	}
}
