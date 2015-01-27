package com.xt.gt.jt.bh;

import com.xt.gt.jt.event.RequestEvent;

/**
 * 方法参数信息类。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  这个类是用来表示一个方法参数的名称和作用域的包装类。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-12
 */
public class MethodParamInfo {


	/**
	 * 参数名称
	 */
	private String name;

	/**
	 * 参数的作用域，分为：请求，会话，应用。
	 */
	private int scope = RequestEvent.REQUEST;
	
	/**
	 * 参数类型:collection-集合
	 */
	private String type;  
	
	/**
	 * 集合类型，指如果参数是集合类型时，集合类型中的类型
	 */
	private Class collectionClass;
	
	public MethodParamInfo () {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public Class getCollectionClass() {
		return collectionClass;
	}

	public void setCollectionClass(Class collectionClass) {
		this.collectionClass = collectionClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
