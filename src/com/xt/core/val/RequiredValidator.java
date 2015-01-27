package com.xt.core.val;

/**
 * 非空校验器。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 判断一个字符串是否为空。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-16
 */
public class RequiredValidator implements IValidator{

	public String getErrorMessage() {
		return "{title}不能为空！";
	}

	public String getRegExpStr(int type) throws ValidatorException {
		if (IValidator.JAVA_SCRIPT == type) {
		    return "required";
		} else {
			return "\\S+"; // 不能有前导空格
		}
	}

	public void setValue(String value) {		
	}

	public String getId () {
		return "required";
	}

}
