package com.xt.core.cm.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import com.xt.core.cm.IClassModifier;
import com.xt.core.cm.NewMethod;
import com.xt.core.utils.StringUtils;
import com.xt.core.utils.cp.PropertyCopyException;
import com.xt.core.utils.cp.PropertyWriter;

/**
 * 
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  存取织入器。将指定的类进行改写，织入属性读取接口，加快属性的操作。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-26
 */
public class PropertyWriterModifier implements IClassModifier {

	public Class getInterface() {
		return PropertyWriter.class;
	}

	public String getName(String modifiedClassName) {
		return modifiedClassName + "_PW";
	}

	public String[] getNewFields(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		return null;
	}

	public NewMethod[] getNewMethods(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod[] nms = new NewMethod[2];
		//逐一添加类的接口方法
		nms[0] = createDuplicate(modifiedClass);
		nms[1] = createSetProperty(modifiedClass);
		return nms;
	}

	public boolean needModify(Class modifidClass) {
		return !modifidClass.equals(PropertyWriter.class);
	}
	
	/**
	 * add the getTableName method to modified class public void
	 * duplicate(Object value) throws PropertyCopyException { if (value == null ||
	 * (value instanceof PropertyWriterDemo)) { throw new
	 * PropertyCopyException(); }
	 * 
	 * PropertyWriterDemo source = (PropertyWriterDemo)value; str1 =
	 * source.str1; int1 = source.int1; pwd = source.pwd; }
	 */
	private NewMethod createDuplicate(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod newMethod = new NewMethod();
		newMethod.setName("duplicate");
		StringBuffer body = new StringBuffer();
		body.append("if ($1 == null || ($1 instanceof ").append(
				modifiedClass.getName()).append(")) {")
				.append("throw new ")
				.append(PropertyCopyException.class.getName())
				.append("();").append("}");
		body.append(modifiedClass.getName()).append(
				" source = (" + modifiedClass.getName() + ")$1;");
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				body.append("set" + StringUtils.capitalize(field.getName()))
				.append("(source.get").append(
						StringUtils.capitalize(field.getName())).append("());");
			}
		}
		newMethod.setBody(body.toString());
		return newMethod;
	}

	/**
	 * add the getTableName method to modified class public void if
	 * ("str1".equals(propertyName)) { str1 = (String)value; } else if
	 * ("int1".equals(propertyName)) { int1 = ((Integer)value).intValue(); }
	 * else if ("pwd".equals(propertyName)) { pwd = (PropertyWriterDemo)value; }
	 * throw new PropertyCopyException();
	 */
	private NewMethod createSetProperty(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		NewMethod newMethod = new NewMethod();
		newMethod.setName("setProperty");
		StringBuffer body = new StringBuffer();
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				if (i > 0) {
					body.append(" else ");
				}
				body
						.append("if (\"" + field.getName() + "\".equals($1)) {")
						.append("set")
						.append(StringUtils.capitalize(field.getName()))
						.append("(" + convertValue(field.getType(), "$2") + ");")
						.append("}");
			}
			body.append(" else ");
			
		}
		
		body.append("throw new ")
			.append(PropertyCopyException.class.getName())
			.append("(\"未发现属性[\" + $1 + \"]\");");
		newMethod.setBody(body.toString());
		return newMethod;
	}
	
	private String convertValue(CtClass fieldType, String value) throws NotFoundException {
		StringBuilder strBld = new StringBuilder();
		if (CtClass.intType.equals(fieldType)) {
			strBld.append("((Integer)").append(value).append(").intValue()");
		} else if (CtClass.longType.equals(fieldType)) {
			strBld.append("((Long)").append(value).append(").longValue()");
		} else if (CtClass.floatType.equals(fieldType)) {
			strBld.append("((Float)").append(value).append(").floatValue()");
		} else if (CtClass.doubleType.equals(fieldType)) {
			strBld.append("((Double)").append(value).append(").doubleValue()");
		} else {
			strBld.append("(").append(fieldType.getName()).append(")").append(value);
		}
		return strBld.toString();
	}

	public void init(Class modifiedClass) {
		
	}

}
