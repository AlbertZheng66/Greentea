package com.xt.gt.jt.bh;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;

/**
 * 这个类中定义了业务处理方法的所有信息。
 * <p>Title: XT框架-持久化部分</p>
 * <p>Description: 这个类定义了一个服务类的所有相关信息（包括包，服务类名称）。
 * 1. packages:业务处理类所在的包，可以为空，也可以包含多级结构，各级之间用“/”分割。
 *             如请求是“/packages/UserMgr.do?method=add”，则包是“packages".
 * 2. name:业务处理类的名称，不能为空，表示请求路径的名称；如请求是“/packages/UserMgr.do?method=add”,
 *         则其名称是“UserMgr”，即去掉前面的包和后面的后缀。
 * 3. handlerClass:业务处理类。
 * </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-6-22
 */

public class BizHandlerInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8291015632601663531L;

	/**
	 * 业务处理类所在的包，可以为空，也可以包含多级结构，各级之间用“/”分割。
	 */
	private String packages;
	
	/**
	 * 业务处理类的名称。
	 */
	private String name;
	
	/**
	 * 业务处理类(服务类)的类型。
	 */
	private Class handlerClass;
	
	/**
	 * 是否需要持久化,默认是需要
	 */
	private boolean needPersistence = true;
	
	/**
	 * 存储所有已经访问过的值(包括从配置文件中载入和通过约定处理的映射关系)，
	 * 键是方法的名称（HTTP请求的参数），值是DealMethodInfo类的实例。
	 */
	private Map<String, DealMethodInfo> dealMethods =
		new HashMap<String, DealMethodInfo>();
	
	/**
	 * 构建函数
	 */
	public BizHandlerInfo () {
	}
	
	/**
	 * 增加一个业务方法，如果增加的方法为空或者方法的名称为空，
	 * 则抛出BadParameterException异常。
	 * @param dmi 处理方法对象
	 */
	public void addDealMethodInfo (DealMethodInfo dmi) {
		if (dmi == null || StringUtils.isBlank(dmi.getName())) {
		    throw new BadParameterException("参数或者参数的名称不能为空");
		}
		dealMethods.put(dmi.getName(), dmi);
	}

	public Class getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(Class clazz) {
		this.handlerClass = clazz;
	}

	public Map getDealMethods() {
		return dealMethods;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedPersistence() {
		return needPersistence;
	}

	public void setNeedPersistence(boolean needPersistence) {
		this.needPersistence = needPersistence;
	}
	
	/**
	 * 根据方法的名称返回业务方法的信息，如果没有找到则返回空。
	 * @param methodName 方法名称
	 * @return 处理方法对象
	 */
	public DealMethodInfo getDealMethod(String methodName) {
		return (DealMethodInfo)dealMethods.get(methodName);
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}
	
	
}
