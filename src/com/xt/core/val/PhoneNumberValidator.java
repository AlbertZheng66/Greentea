package com.xt.core.val;

/**
 * 电话号码校验器
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-12-19
 */
public class PhoneNumberValidator implements IValidator {

	private String name;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}只包含数字和横线“-”，" +
				"格式为：国际区号-国内区号-电话号码。例如：0086-010-88882222, " +
				"010-88882222, 88882222");
		
		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		//国际区号-国内区号-电话号码-分机号
		String regExp = "^(\\d{3,4}-)?(\\d{3,4}-)?\\d{7,8}(-\\d{3,4})?$";
		
		return regExp;
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "phone";
	}

}
