package com.xt.proxy;

public interface Task {
	
	/**
	 * 当调用结束且没有发生异常时，调用此方法。
	 * @param result
	 */
	public void onComplete(Object result);
	
	/**
	 * 当调用发生异常时，将调用此方法。
	 * @param t
	 */
	public void onError(Throwable t);
	
	/**
	 * 当调用结束时，无论是否发生异常，都将调用此方法。
	 * @param t
	 */
	public void onFinally();

}
