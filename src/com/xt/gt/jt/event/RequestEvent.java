package com.xt.gt.jt.event;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.BadParameterException;

/**
 * <p>
 * Title: 框架类.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public interface RequestEvent {

//	/**
//	 * 作用域，从请求参数中读取
//	 */
//	public static final int PARAMETER = 0;
	/**
	 * 作用域，一个请求范围内
	 */
	public static final int REQUEST = 0;

	/**
	 * 作用域，一个请求范围内
	 */
	public static final int SESSION = 1;

	/**
	 * 作用域，一个请求范围内
	 */
	public static final int APPLICATION = 2;
	
	/**
	 * 表示在所有的作用域范围内进行查找
	 */
	public final static int ALL = 4;


	/**
	 * 从当前的Http请求连接中得到其属性值
	 * 
	 * @param paramName
	 *            String 属性名称
	 * @return Object 如果未发现以此命名的对象,返回空
	 */
	public Object getAttribute(String paramName);

	/**
	 * 根据产生的名称返回相应的对象。
	 * 1.首先在Http请求的产生中查找相应的对象，如果发现，将其转形,并返回此对象；
	 * 2.如果未发现，在session中查找，如果发现，将其转形,并返回此对象；
	 * 3.如果未发现，在Servlet上下文中查找，如果发现，将其转形,并返回此对象；
	 *  4.如果未发现，返回空。
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public Object findAttribute(String paramName);

	/**
	 * 在指定的范围内查找指定属性的值
	 * 
	 * @param paramName
	 *            String 属性名称
	 * @param scope
	 *            String 查找的范围(request, session, application)
	 * @return Object 如果未发现以此命名的对象,返回空
	 */
	public Object getAttribute(String paramName, int scope);

	/**
	 * 根据产生的名称返回相应的对象。查找的顺序如getObject方法，并将其转型（Cast）
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public String getParameter(String paramName);

	/**
	 * 返回数组类型的参数值
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public String[] getParameterValues(String paramName);

	/**
	 * 返回HTTP请求的参数
	 */
	public String[] getParameterNames();

	/**
	 * 根据产生的名称返回相应的对象。查找的顺序如getString方法，并将其转型（Cast）
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public int getInt(String paramName);

	/**
	 * 根据产生的名称返回相应的对象。查找的顺序如getString方法，并将其转型（Cast）
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public double getDouble(String paramName);

	/**
	 * 根据产生的名称返回相应的对象。查找的顺序如getString方法，并将其转型（Cast）
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public long getLong(String paramName);

	/**
	 * 根据产生的名称返回相应的对象。查找的顺序如getString方法，并将其转型（Cast）
	 * 
	 * @param paramName
	 *            参数名称
	 * @return 相应的对象
	 */
	public float getFloat(String paramName);
	
	/**
	 * 当请求参数中有文件上传时，返回相应的输入流；
	 * 如果请求非多部分请求(“multipart/form-data”)，则返回空.
	 * @param name
	 * @return
	 */
	public InputStream getInputStream(String name);

	/**
	 * 返回数组形式的数据。
	 * 
	 * @param collName
	 *            集合的名称
	 * @param clazz
	 *            集合中的对象的类型
	 * @return 数组集合
	 */
	public Object[] getObjects(String collName, Class clazz)
			throws BadParameterException;

	public HttpServletRequest getRequest();

}
