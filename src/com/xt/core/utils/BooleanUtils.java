package com.xt.core.utils;

public class BooleanUtils {
	
	
	 /**
	  * true|y|1|yes|on
	  */
	 public static boolean isTrue (String str) {
    	 
    	 return ("yes".equalsIgnoreCase(str) || "1".equalsIgnoreCase(str) 
    			 || "true".equalsIgnoreCase(str)  || "on".equalsIgnoreCase(str)
    			  || "y".equalsIgnoreCase(str));
     }
	 
	 /**
	  * true|y|1|yes|on
	  */
	 public static boolean isTrue (String str, boolean defaultValue) {
		 if (str == null) {
			 return defaultValue;
		 }
    	 return isTrue(str);
     }
}
