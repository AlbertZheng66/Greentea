package com.xt.core.val;

public class EmailValidator implements IValidator {

	private String name;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("请确认输入的{title}是否正确！" +
				"例如:aaa@xxx.com");
		
		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		String regExp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		
		return regExp;
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "email";
	}

}
