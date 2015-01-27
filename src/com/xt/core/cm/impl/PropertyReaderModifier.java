package com.xt.core.cm.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import com.xt.core.cm.IClassModifier;
import com.xt.core.cm.NewMethod;
import com.xt.core.utils.StringUtils;
import com.xt.core.utils.cp.PropertyCopyException;
import com.xt.core.utils.cp.PropertyReader;

/**
 * 
 * <p>
 * Title: XT框架-事务逻辑部分-属性读取接口更改器。
 * </p>
 * <p>
 * Description:采用字节码注入方式实现一个新类，使一个类动态的实现属性读取接口。
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
 * @date 2006-8-24
 */
public class PropertyReaderModifier implements IClassModifier {

	// 生成后的新属性读取器名称的后缀
	private static final String subfix = "_PR";

	public Class getInterface() {
		return PropertyReader.class;
	}

	public String getName(String modifiedClassName) {
		return modifiedClassName + subfix;
	}

	public String[] getNewFields(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		// 无须添加新的属性
		return null;
	}

	public NewMethod[] getNewMethods(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod[] nms = new NewMethod[3];
		// 逐一添加类的接口方法
		nms[0] = createGetPropertyNames(modifiedClass);
		nms[1] = createGetPropertyValue(modifiedClass);
		nms[2] = createGetPropertyClass(modifiedClass);
		return nms;
	}

	/**
	 * add the getTableName method to modified class public void public String[]
	 * getPropertyNames() throws PropertyCopyException; return new String[]
	 * {"str1", "int1", "pwd"};
	 */
	private NewMethod createGetPropertyNames(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod newMethod = new NewMethod();
		newMethod.setName("getPropertyNames");
		StringBuffer body = new StringBuffer();
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null) {
			body.append("String[] pns = new String[" + fields.length + "];");
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				body.append("pns[" + i + "] = \"" + field.getName() + "\";");
			}
		}

		body.append("return pns;");
		newMethod.setBody(body.toString());
		return newMethod;
	}

	/**
	 * add the getTableName method to modified class public void if public
	 * Object getPropertyValue(String propertyName) throws PropertyCopyException {
	 * Object ret = null; if ("str1".equals(propertyName)) { ret = getStr1(); }
	 * else if ("int1".equals(propertyName)) { ret = new Integer(getInt1()); }
	 * else if ("pwd".equals(propertyName)) { ret = getPwd(); } else { throw new
	 * PropertyCopyException("未发现属性[" + propertyName + "]"); } return ret; }
	 */
	private NewMethod createGetPropertyValue(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod newMethod = new NewMethod();
		newMethod.setName("getPropertyValue");
		StringBuffer body = new StringBuffer("Object ret = null;");
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				if (i > 0) {
					body.append(" else ");
				}
				body.append("if (\"" + field.getName() + "\".equals($1)) {")
						.append("ret = ($w)get").append(
								StringUtils.capitalize(field.getName()))
						.append("(); }");

			}
			body.append(" else ");
		}

		body.append("throw new ").append(PropertyCopyException.class.getName())
				.append("(\"未发现属性[\" + $1 + \"]\");");
		body.append("return ret;");
		newMethod.setBody(body.toString());
		return newMethod;
	}

	/**
	 * add the getTableName method to modified class public void if public Class
	 * getPropertyClass(String propertyName) throws PropertyCopyException {
	 * Class pc = null; if ("str1".equals(propertyName)) { pc = String.class; }
	 * else if ("int1".equals(propertyName)) { pc = Integer.class; } else if
	 * ("pwd".equals(propertyName)) { pc = PropertyWriterDemo.class; } else {
	 * throw new PropertyCopyException("未发现属性[" + propertyName + "]"); } return
	 * pc; }
	 */
	private NewMethod createGetPropertyClass(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod newMethod = new NewMethod();
		newMethod.setName("getPropertyClass");
		StringBuffer body = new StringBuffer("Class pc = null;");
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				if (i > 0) {
					body.append(" else ");
				}
				body.append("if (\"" + field.getName() + "\".equals($1)) {")
						.append("pc = ").append(field.getType().getName())
						.append(".class").append("; }");
			}
			body.append(" else ");

		}

		body.append("throw new ").append(PropertyCopyException.class.getName())
				.append("(\"未发现属性[\" + $1 + \"]\");");

		body.append("return pc;");
		newMethod.setBody(body.toString());
		return newMethod;
	}

	public boolean needModify(Class modifidClass) {
		return !modifidClass.equals(PropertyReader.class);
	}

	public void init(Class modifiedClass) {		
	}

}
