package com.xt.gt.jt.proc.result.ajax;

import java.util.Collection;
import java.util.Iterator;

public class CollectionToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"list\" ");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		Collection coll = (Collection) value;
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			ToAjax toAjax = AjaxFactory.getToAjax(obj);
			strBld.append("<value  ");
			toAjax.appendAttributes(strBld, obj);
			strBld.append(" >");
			toAjax.appendChildren(strBld, obj);
			strBld.append("</value>");
		}
	}
}
