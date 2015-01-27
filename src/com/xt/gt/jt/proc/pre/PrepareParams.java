package com.xt.gt.jt.proc.pre;

import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.event.RequestEvent;

/**
 * 
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-10
 */
public interface PrepareParams {

	/**
	 * 返回业务处理方法输入参数的类型。如果为空，则返回null。
	 * @param bizHandler 业务处理实例，传入时不为空
	 * @param dmInfo 业务处理方法的信息，不为空
	 * @param req    HttpServletRequest实例
	 * @return 参数类型数组
	 */
	public Class[] getParamClasses(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent);

	/**
	 * 返回业务处理方法输入参数的值；如果为空，则返回null。
	 * @param bizHandler 业务处理实例，传入时不为空
	 * @param dmInfo 业务处理方法的信息，不为空
	 * @param req    HttpServletRequest实例
	 * @return 参数值数组
	 */
	public Object[] getParams(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent requestEvent);

}
