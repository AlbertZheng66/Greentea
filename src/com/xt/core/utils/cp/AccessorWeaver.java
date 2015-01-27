package com.xt.core.utils.cp;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import com.xt.core.cm.ClassModifyException;
import com.xt.core.exception.BadParameterException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.StringUtils;

/**
 * <p>
 * Title: XT框架-核心部分
 * </p>
 * <p>
 * Description: 存取织入器。将指定的类进行改写，织入属性读取接口，加快属性的操作。
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
 * @date 2006-4-18
 */

public class AccessorWeaver {

	private static final Class DESTINATION = PropertyWriter.class;

	// 生成后的新PO名称的后缀
	private static final String subfix = "_PW";

	private ClassPool pool;

	private CtClass newCtClass = null;

	public AccessorWeaver() {
		ClassPath cp = new ClassClassPath(getClass());
		pool = ClassPool.getDefault();
		pool.appendClassPath(cp);
	}

	/**
	 * 新类是否转换过,不在此进行校验
	 */
	public synchronized Class modify(Class modifiedClass)
			throws ClassModifyException {
		// 校验
		if (null == modifiedClass) {
			throw new BadParameterException("被转换的类不能为空!");
		}

		// 已经被实现或者织入
		if (modifiedClass == PropertyWriter.class) {
			return modifiedClass;
		}

		Class newClass = null;
		try {
			CtClass modified = pool.get(modifiedClass.getName());

			// 新类的名称
			String className = modifiedClass.getName() + subfix;

			// 产生一个新类
			newCtClass = pool.makeClass(className, modified);

			CtClass pw = pool.get(PropertyWriter.class.getName());

			// 增加接口
			newCtClass.addInterface(pw);

			// implements the interface
			// add fields

			createDuplicate(pw, modified);

			createSetProperty(pw, modified);

			newClass = newCtClass.toClass();
		} catch (NotFoundException ex) {
			throw new ClassModifyException(ex);
		} catch (CannotCompileException ex) {
			throw new ClassModifyException(ex);
		} catch (Exception ex) {
			throw new ClassModifyException(ex);
		}

		return newClass;
	}

	// if the class has been modified, it should not modified twice
	public boolean needModify(Class modifidClass) {
		return !modifidClass.equals(DESTINATION);
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
	private void createDuplicate(CtClass pw, CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
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
				//setStr1(source.getStr1());
				body.append("set" + StringUtils.capitalize(field.getName()))
				.append("(source.get").append(
						StringUtils.capitalize(field.getName())).append("());");
			}
		}

		addMethod(pw, "duplicate", body.toString());
	}

	/**
	 * add the getTableName method to modified class public void if
	 * ("str1".equals(propertyName)) { str1 = (String)value; } else if
	 * ("int1".equals(propertyName)) { int1 = ((Integer)value).intValue(); }
	 * else if ("pwd".equals(propertyName)) { pwd = (PropertyWriterDemo)value; }
	 * throw new PropertyCopyException();
	 */
	private void createSetProperty(CtClass pw, CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
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
		addMethod(pw, "setProperty", body.toString());
	}

	private void addMethod(CtClass ipo, String methodName, String body)
			throws CannotCompileException, NotFoundException {
		LogWriter.debug(methodName + " body=" + body.toString());
		// 增加实现接口的方法
		CtMethod method = ipo.getDeclaredMethod(methodName);
		CtMethod newMethod = CtNewMethod.make(method.getReturnType(),
				methodName, method.getParameterTypes(), method
						.getExceptionTypes(), "{" + body + "}", newCtClass);
		newCtClass.addMethod(newMethod);
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
}