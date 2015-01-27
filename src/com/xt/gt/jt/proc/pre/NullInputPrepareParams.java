package com.xt.gt.jt.proc.pre;

import java.util.HashSet;
import java.util.Set;

import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.event.RequestEvent;

/**
 * 空参数方法
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-2
 */
public class NullInputPrepareParams  implements PrepareParams {

	/**
	 * 构建函数
	 */
	public NullInputPrepareParams() {

	}

	/**
	 * 系统参数，不能作为某个服务的输入参数
	 */
	private static Set<String> systemParams = new HashSet<String>();

	static {
		// 不能反射的参数
		systemParams.add("bizHandler");
		systemParams.add("method");
	}

	public Class[] getParamClasses(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent req) {
		Class[] paramClasses = new Class[0];
		return paramClasses;
	}

	public Object[] getParams(Object bizHandler, DealMethodInfo dmInfo,
			RequestEvent req) {
		Object[] params = new Object[0];
		return params;
	}
}
