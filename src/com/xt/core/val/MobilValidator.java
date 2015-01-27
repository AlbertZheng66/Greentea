package com.xt.core.val;

public class MobilValidator implements IValidator {

	private String name;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}只包含数字和横线“-”，" +
				"格式为：国际区号-手机号。例如：0086-13599999999");
		
		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		//国际区号-手机号
		String regExp = "^(\\d{3,4}-)?0?1\\d{10,10}$";
		
		return regExp;
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "mobil";
	}

}
