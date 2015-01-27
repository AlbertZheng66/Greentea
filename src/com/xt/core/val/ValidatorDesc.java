package com.xt.core.val;


public @interface ValidatorDesc {
	
	/**
	 * 校验的配置
	 * @return
	 */
	ValidatorType type() default ValidatorType.CUSTOMIZED;
	
	/**
	 * 校验的参数，参数的含义由具体的类型自行确定。
	 * @return
	 */
	String params() default "";
	

}
