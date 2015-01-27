package com.xt.core.cm;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;


/**
 * 类修改接口,用户方便对类进行修改,动态的增加接口等工作.
 * <p>
 * Title: XT框架-基础结构部分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-22
 */
public interface IClassModifier {
	
	public void init (Class modifiedClass);
	
	/**
	 * 返回新类的名称，如果返回的为空，则表示在原来的类上进行修改。
	 * @param modifiedClass
	 * @return
	 */
	public String getName(String modifiedClassName);

	public NewMethod[] getNewMethods(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException;

	/**
	 * 返回需要增加的类型。
	 * @param modifiedClass
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public String[] getNewFields(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException;

	public Class getInterface();

	/**
	 * 用户可以自定义自己的修改类型，甚至可以根据名称的规则进行修改。
	 *  同时，如果类已经被修改过，就不能再被修改。
	 */
	public boolean needModify(Class modifidClass);
}
