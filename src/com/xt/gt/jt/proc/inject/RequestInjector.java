package com.xt.gt.jt.proc.inject;

import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.DealMethodInfo;

/**
 * 服务注入器。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 用户可以实现该接口，以注入相应的参数。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-7
 */
public interface RequestInjector {
	
	/**
	 * 是否需要注入。
	 * @return
	 */
    public boolean needInject (BizHandlerInfo bizHandlerInfo, DealMethodInfo dmInfo);
    
    /**
     * 注入变量，并返回被注入的变量
     * @param bizHandler
     */
    public Object inject (Object bizHandler);
}
