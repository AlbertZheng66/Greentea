package com.xt.gt.jt.proc.result.ajax;

import java.util.Iterator;

import com.xt.gt.jt.event.DefaultResponseEvent;

public class DefaultResponseEventToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"map\" ");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		DefaultResponseEvent dre = (DefaultResponseEvent) value;
		for (Iterator iter = dre.getAttributeNames().iterator(); iter.hasNext();) {
			String key = (String)iter.next();
			strBld.append("<key name=\"" + key + "\"");
			Object rsdValue = dre.getValue(key);
			ToAjax toAjax = AjaxFactory.getToAjax(rsdValue);
			toAjax.appendAttributes(strBld, rsdValue);
			strBld.append(" >");
			toAjax.appendChildren(strBld, rsdValue);
			strBld.append("</key>");
		}
	}
}