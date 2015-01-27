package com.xt.core.val;

public class HttpValidator  implements IValidator {

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}不合法。" +
				"例如:http://www.example.com");
		
		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		String regExp = "^http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$";
		
		return regExp;
	}

	public void setValue(String value) {
	}


	public String getId () {
		return "http";
	}
}
