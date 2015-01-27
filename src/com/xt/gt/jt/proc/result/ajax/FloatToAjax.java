package com.xt.gt.jt.proc.result.ajax;

public class FloatToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		double ret = 0;
		if (value instanceof Float) {
		    ret = (Float) value;
		} else if (value instanceof Double) {
			ret = (Double)value;
		}
		strBld.append(" type=\"double\" ");
		strBld.append(" value=\"").append(ret).append("\"");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
	}
}
