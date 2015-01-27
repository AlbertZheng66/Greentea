package com.xt.gt.jt.proc.inject;

/**
 * 请求处理器。
 * @author albert
 *
 */
public interface RequestProcessor {

	/**
	 * 在应用程序初始化时调用此方法。
	 */
	public void applicationInit ();
	
	/**
	 * 在调用业务处理之前触发此方法。
	 */
	public void beforeProcess();
	

	/**
	 * 在调用业务处理之后触发此方法。
	 */
	public void afterProcess();

	/**
	 * 业务处理的异常发生时调用此方法。
	 * @param t
	 */
	public void onError (Throwable t);
	
}
