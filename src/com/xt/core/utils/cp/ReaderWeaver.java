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

public class ReaderWeaver {

	private static final Class DESTINATION = PropertyReader.class;

	// 生成后的新PO名称的后缀
	private static final String subfix = "_RW";

	private ClassPool pool;

	private CtClass newCtClass = null;

	public ReaderWeaver() {
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
		if (modifiedClass == PropertyReader.class) {
			return modifiedClass;
		}

		Class newClass = null;
		try {
			CtClass modifiedCtClass = pool.get(modifiedClass.getName());

			// 新类的名称
			String className = modifiedClass.getName() + subfix;

			// 产生一个新类
			newCtClass = pool.makeClass(className, modifiedCtClass);

			CtClass pr = pool.get(PropertyReader.class.getName());

			// 增加接口
			newCtClass.addInterface(pr);

			// implements the interface
			// add fields

			createGetPropertyNames(pr, modifiedCtClass);

			createGetPropertyValue(pr, modifiedCtClass);
			
			createGetPropertyClass(pr, modifiedCtClass);

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
	 * public String[] getPropertyNames() throws PropertyCopyException;
	 * return new String[] {"str1", "int1", "pwd"};
	 */
	private void createGetPropertyNames(CtClass pr, CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
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

		addMethod(pr, "getPropertyNames", body.toString());
	}

	/**
	 * add the getTableName method to modified class public void if
	 *public Object getPropertyValue(String propertyName) throws PropertyCopyException {
		Object ret = null;
		if ("str1".equals(propertyName)) {
			ret = getStr1();
		} else if ("int1".equals(propertyName)) {
			ret = new Integer(getInt1());
		} else if ("pwd".equals(propertyName)) {
			ret = getPwd();
		} else {
			throw new PropertyCopyException("未发现属性[" + propertyName + "]");
		}
		return ret;
	}
	 */
	private void createGetPropertyValue(CtClass pr, CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		StringBuffer body = new StringBuffer("Object ret = null;");
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				if (i > 0) {
					body.append(" else ");
				}
				body
						.append("if (\"" + field.getName() + "\".equals($1)) {")
						.append("ret = ")
						.append(convertValue(field.getType(), 
								StringUtils.capitalize(field.getName())))
						.append(" }");
			}
			body.append(" else ");			
		}
		
		body.append("throw new ")
			.append(PropertyCopyException.class.getName())
			.append("(\"未发现属性[\" + $1 + \"]\");");
		body.append("return ret;");
		addMethod(pr, "getPropertyValue", body.toString());
	}
	
	/**
	 * add the getTableName method to modified class public void if
	 *public Class getPropertyClass(String propertyName) throws PropertyCopyException {
		Class pc = null;
		if ("str1".equals(propertyName)) {
			pc = String.class;
		} else if ("int1".equals(propertyName)) {
			pc = Integer.class;
		} else if ("pwd".equals(propertyName)) {
			pc = PropertyWriterDemo.class;
		} else {
			throw new PropertyCopyException("未发现属性[" + propertyName + "]");
		}
		return pc;
	}
	 */
	private void createGetPropertyClass(CtClass pr, CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		StringBuffer body = new StringBuffer("Class pc = null;");
		CtField[] fields = modifiedClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				CtField field = fields[i];
				if (i > 0) {
					body.append(" else ");
				}
				body
						.append("if (\"" + field.getName() + "\".equals($1)) {")
						.append("pc = ")
						.append(field.getType().getName()).append(".class")
						.append("; }");
			}
			body.append(" else ");
			
		}
		
		body.append("throw new ")
			.append(PropertyCopyException.class.getName())
			.append("(\"未发现属性[\" + $1 + \"]\");");
		
		body.append("return pc;");
		addMethod(pr, "getPropertyClass", body.toString());
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
	
	/**
	 * .append("ret = ")
						.append(StringUtils.capitalize(field.getName()))
						.append("(); }");
	 * @param fieldType
	 * @return
	 * @throws NotFoundException
	 */
	private String convertValue(CtClass fieldType, String fieldName) throws NotFoundException {
		StringBuilder strBld = new StringBuilder();
		if (CtClass.intType.equals(fieldType)) {
			strBld.append("new Integer(get").append(fieldName).append("());");
		} else if (CtClass.longType.equals(fieldType)) {
			strBld.append("new Long(get").append(fieldName).append("());");
		} else if (CtClass.floatType.equals(fieldType)) {
			strBld.append("new Float(get").append(fieldName).append("());");
		} else if (CtClass.doubleType.equals(fieldType)) {
			strBld.append("new Double(get").append(fieldName).append("());");
		} else {
			strBld.append("get").append(fieldName).append("();");
		}
		return strBld.toString();
	}
}