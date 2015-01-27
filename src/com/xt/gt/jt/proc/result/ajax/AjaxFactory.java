package com.xt.gt.jt.proc.result.ajax;

import java.util.Collection;
import java.util.Map;

import com.xt.gt.jt.event.DefaultResponseEvent;

public class AjaxFactory {
	public AjaxFactory() {
	}

	
	public static ToAjax getToAjax(Object value) {
		ToAjax toAjax = null;
		if (null == value) {
			toAjax = new NullToAjax();
		} else if (value instanceof Boolean) {
			toAjax = new BooleanToAjax();
		} else if (value instanceof Integer || value instanceof Long) {
			toAjax = new IntToAjax();
		}  else if (value instanceof Float || value instanceof Double) {
			toAjax = new FloatToAjax();
		}  else if (value instanceof String) {
			toAjax = new StringToAjax();
		} else if (value instanceof Collection) {
			toAjax = new CollectionToAjax();
		} else if (value instanceof Map) {
			toAjax = new MapToAjax();
		} else if (value instanceof DefaultResponseEvent) {
			toAjax = new DefaultResponseEventToAjax();
		} else {
			toAjax = new ObjectToAjax();
		}
		return toAjax;
	}
}
