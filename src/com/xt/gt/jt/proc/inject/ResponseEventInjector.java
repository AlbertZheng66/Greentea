package com.xt.gt.jt.proc.inject;

import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.DealMethodInfo;
import com.xt.gt.jt.event.DefaultResponseEvent;
import com.xt.gt.jt.event.ResponseEvent;

/**
 * 
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-7
 */
public class ResponseEventInjector  implements RequestInjector{

	//如果服务类定义了“responseEvent”属性，则注入responseEvent对象
	public Object inject(Object bizHandler) {
		ResponseEvent responseEvent = null;
		// 注入返回事件,方便返回参数
		if (null != ClassHelper.getMethod(bizHandler, "setResponseEvent",
				new Class[] { ResponseEvent.class })) {
			responseEvent = new DefaultResponseEvent();
			BeanHelper.copyProperty(bizHandler, "responseEvent",
					responseEvent);
		}
		return responseEvent;
	}

	public boolean needInject(BizHandlerInfo bizHandlerInfo, DealMethodInfo dmInfo) {
		return true;
	}

}
