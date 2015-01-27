package com.xt.gt.jt.proc.result.ajax;

import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;

public class ObjectToAjax implements ToAjax {

	public void appendAttributes(StringBuilder strBld, Object value) {
		strBld.append(" type=\"Object\" className=\"").append(
				value.getClass().getName()).append("\"");
	}

	public void appendChildren(StringBuilder strBld, Object value) {
		String pns[] = ClassHelper.getPropertyNames(value.getClass());
		for (int i = 0; i < pns.length; i++) {
			strBld.append("<field ");
			strBld.append(" name=\"").append(pns[i]).append("\"");
			Object fieldValue = BeanHelper.getProperty(value, pns[i]);
			ToAjax toAjax = AjaxFactory.getToAjax(fieldValue);
			toAjax.appendAttributes(strBld, fieldValue);
			strBld.append(" >");
			toAjax.appendChildren(strBld, fieldValue);
			strBld.append("</field>");
		}		
	}
}
