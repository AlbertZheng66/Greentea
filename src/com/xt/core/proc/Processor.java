package com.xt.core.proc;

import java.lang.reflect.Method;

import com.xt.core.session.Session;
import com.xt.proxy.Context;

public interface Processor {
	
	/**
	 * 创建类之后调用此方法。
	 * @param service
	 */
	public void onCreate (Class serviceClass, Session session, Context context);
	
	/**
	 * 业务处理方法之前调用此接口
	 * @param service 业务逻辑对象
	 * @param method  调用的方法
	 * @param params  调用的参数
     * @return 处理后的参数（注意：不要改变参数的个数；如果不需要处理，可直接返回传入参数）。
	 */
	public Object[] onBefore (Object service, Method method, Object[] params);
	
	/**
	 * 业务处理方法之后调用此方法（发生异常时不调用此方法）
	 * @param service 业务逻辑对象
	 * @param ret     返回值
	 */
	public void onAfter (Object service, Object ret);
	
	/**
	 * 发生异常时框架将调用此方法。
	 * @param t 异常对象
	 */
	public void onThrowable (Throwable t);
	
	/**
	 * 所有事物处理之后调用此方法。
	 */
	public void onFinally ();
	

        /**
         * 所有处理都已经完成时（比如：Servlet已经将结果返回给客户端，即输出流已经结束）将调用此方法。
         */
        public void onFinish();
}
