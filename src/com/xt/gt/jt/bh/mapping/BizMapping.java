package com.xt.gt.jt.bh.mapping;

import com.xt.gt.jt.bh.BizHandlerInfo;
import com.xt.gt.jt.bh.DealMethodInfo;

/**
 *  事务逻辑映射。
 * <p>Title: GT框架-事务逻辑部分</p>
 * <p>Description:  将一个HTTP请求映射为相应的映射业务处理类即处理方法。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-11-6
 */
public interface BizMapping {

	/**
	 * 根据包名称返回事务处理的类
	 * @param packageName 
	 * @param bizHandler
	 */
	public BizHandlerInfo getBizHandlerInfo (String packageName, String bizHandler);
	
	/**
	 * 返回处理方法的具体信息。
	 * @param bizHandlerInfo 业务处理类信息。
	 * @param methodName 方法的名称（指客户端调用时使用的名称，如果客户端是浏览器，则是调用的参数）。
	 * @return 
	 */
	public DealMethodInfo getDealMethodInfo (BizHandlerInfo bizHandlerInfo, String methodName);
	

}
