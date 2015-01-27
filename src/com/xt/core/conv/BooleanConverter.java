package com.xt.core.conv;

public class BooleanConverter {
	
	static public boolean convert(Boolean wrapper) {
		if (wrapper == null) {
			return false;
		}
		return wrapper.booleanValue();
	}

}
