package com.xt.gt.jt.proc.result.ajax;

import java.util.Iterator;
import java.util.Map;

public class MapToAjax  implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"map\" ");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		Map map = (Map) value;
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			strBld.append("<key name=\"" + key.toString() + "\"");
			Object mapValue = map.get(key);
			ToAjax toAjax = AjaxFactory.getToAjax(mapValue);
			toAjax.appendAttributes(strBld, mapValue);
			strBld.append(" >");
			toAjax.appendChildren(strBld, mapValue);
			strBld.append("</key>");
		}
	}
}

