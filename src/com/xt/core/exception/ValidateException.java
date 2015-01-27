
package com.xt.core.exception;

/**
 * 这个类只在原有的JSP框架中使用，建议在项目中不再使用此框架类。
 * @deprecated
 * @author albert
 */
public class ValidateException extends ServiceException{
    private String code;


	public ValidateException(String code) {
		super(code);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
