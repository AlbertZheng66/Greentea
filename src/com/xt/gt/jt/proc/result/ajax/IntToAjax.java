package com.xt.gt.jt.proc.result.ajax;

public class IntToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		long ret = 0l;
		if (value instanceof Integer) {
			ret = ((Integer) value).intValue();
		} else if (value instanceof Long) {
			ret = ((Long) value).longValue();
		} 
		strBld.append(" type=\"int\" ");
		strBld.append(" value=\"").append(ret).append("\"");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
	}
}
