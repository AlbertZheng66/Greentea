package com.xt.core.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;

/**
 * 
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 类修改器。
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
 * @date 2006-8-23
 */

public class ClassModifierFactory {
	
	private final Logger logger = Logger.getLogger(ClassModifierFactory.class);

	private ClassPool pool;

	private List<IClassModifier> classModifiers = new ArrayList<IClassModifier>(
			3);

	private static ClassModifierFactory instance = new ClassModifierFactory();

	private Map<Key, Class> modifiedClasses = new HashMap<Key, Class>();

	private ClassModifierFactory() {
		ClassPath cp = new ClassClassPath(getClass());
		pool = ClassPool.getDefault();
		pool.appendClassPath(cp);
	}

	synchronized public static ClassModifierFactory getInstance() {
		return instance;
	}

	/**
	 * 注册一个类修改器。
	 * 
	 * @param cm
	 */
	synchronized public void register(IClassModifier cm) {
		// 保证不能重复加载,同类的类注册器也允许注册一个。
		if (cm != null && !classModifiers.contains(cm)) {
			LogWriter.info(logger, String.format("注册类修改器[%s]。", cm.getClass().getName()));
			for (Iterator iter = classModifiers.iterator(); iter.hasNext();) {
				IClassModifier modifier = (IClassModifier) iter.next();
				if (modifier.getClass() == cm.getClass()) {
					return;
				}
			}
			classModifiers.add(cm);
		}
	}

	synchronized public Class modify(Class modified) {
		Class newClass = modified;
		for (Iterator iter = classModifiers.iterator(); iter.hasNext();) {
			IClassModifier cm = (IClassModifier) iter.next();
			cm.init(modified);
			if (cm.needModify(modified)) {
				Key key = new Key(modified, cm.getInterface());
				//判断类是否已经被加载过,如果加载过，则不进行此次修改
				Class hasModifiedClass = modifiedClasses.get(key);
				if (hasModifiedClass != null) {
					newClass = hasModifiedClass;
					continue;
				}
				
				// 如果注册了多个类修改器，类将连续被修改
				newClass = modify(cm, newClass);
				modifiedClasses.put(key, newClass);
			}
		}
		return newClass;
	}
	
	/**
	 * 返回类更改后的新名称（此时可能尚未更改此类）。
	 * @param modified 被更改的类
	 * @return
	 */
	synchronized public String getNewName(Class modified) {
		String newClassName = modified.getName();
		LogWriter.debug(logger, "classModifiers.size()=", classModifiers.size());
		for (Iterator<IClassModifier> iter = classModifiers.iterator(); iter.hasNext();) {
			IClassModifier cm = iter.next();
			cm.init(modified);
			LogWriter.debug(logger, "cm.needModify(modified)=", cm.needModify(modified));
			if (cm.needModify(modified)) {
			    newClassName = cm.getName(newClassName);
			}
		}
		return newClassName;
	}

	/**
	 * 新类是否转换过,不在此进行校验
	 */
	private synchronized Class modify(IClassModifier classModifier,
			Class modifiedClass) throws ClassModifyException {
		// 校验
		if (null == modifiedClass) {
			throw new BadParameterException("被转换的类不能为空!");
		}

		Class newClass = null;
		try {
			CtClass modifiedCtClass = pool.get(modifiedClass.getName());

			// 新类的名称
			String className = classModifier.getName(modifiedClass.getName());

			// 判断新类是否存在
			try {
				Class.forName(className);
				throw new SystemException("类[" + className + "]已经存在!");
			} catch (ClassNotFoundException e) {
			}

			// 产生一个新类
			CtClass newCtClass = pool.makeClass(className, modifiedCtClass);

			// 得到需要插入的接口
			Class clazz = classModifier.getInterface();
			if (clazz == null) {
				throw new ClassModifyException("扩展接口不能为空！");
			}
			CtClass newInterface = pool.get(clazz.getName());

			// 增加接口
			newCtClass.addInterface(newInterface);

			// 增加属性
			String[] newFields = classModifier.getNewFields(modifiedCtClass);
			if (newFields != null) {
				for (int i = 0; i < newFields.length; i++) {
					String newField = newFields[i];

					if (StringUtils.isEmpty(newField)) {
						// 出现空属性，程序继续运行
						continue;
					}
					
//					 检查待修改的类中是否已经存在相同的属性,如果出现相同属性，应该是覆盖掉（override）
					CtField field = CtField.make(newField, newCtClass);
					newCtClass.addField(field);
				}
			}

			//开始增加方法
			NewMethod[] newMethods = classModifier
					.getNewMethods(modifiedCtClass);
			if (newMethods != null) {
				for (int i = 0; i < newMethods.length; i++) {
					NewMethod newMethod = newMethods[i];

					if (newMethod == null) {
						// 出现空方法，程序继续运行
						continue;
					}

					// 检查待修改的类中是否已经存在方法名称相同，且参数一致的方法。
                    if (newMethod.isByInterface()) {
                    	//通过接口加入方法
					    addMethod(newCtClass, newInterface, newMethod.getName(),
							newMethod.getBody());
                    } else {
//                    	加入的方法在接口中无定义
					    addMethod(newCtClass, newMethod);
                    }
				}
			}

			newClass = newCtClass.toClass();
		} catch (NotFoundException ex) {
			throw new ClassModifyException(ex);
		} catch (CannotCompileException ex) {
			LogWriter.error(logger, "类编译异常", ex);
			throw new ClassModifyException(ex);
		} catch (Exception ex) {
			LogWriter.error(logger, "未知异常", ex);
			throw new ClassModifyException(ex);
		}

		return newClass;
	}

	/**
	 * 根据原始类和修改后的类名称，返回其新类的字节码（其目的可能是在其他的虚拟机上进行加载）。
	 * @param oldClass    原始类
	 * @param newClassName 修改后的类名称（原始类和新类的名称一定要一致）
	 * @return
	 */
	synchronized public byte[] getClassByteCode(String newClassName) {
		if (newClassName == null) {
			throw new SystemException("原始类和类名称皆不能为空。"); 
		}
//		Class temp = modify(oldClass);
//		if (null == temp) {
//			throw new SystemException(String.format("修改类[%s]发生错误。", oldClass.getName()));
//		}
//		if (!temp.getName().equals(newClassName)) {
//			throw new SystemException(String.format("修改类[%s]名称和指定名称[%s]不一致。", temp.getName(), newClassName));
//		}
		try {
			return pool.getCtClass(newClassName).toBytecode();
		} catch (Exception e) {
			throw new SystemException(String.format("读取类名称[%s]时发生错误。", newClassName), e); 
		}
	}
	
	private void addMethod(CtClass newCtClass, CtClass newInterface,
			String methodName, String body) throws CannotCompileException,
			NotFoundException {
		LogWriter.debug(logger, methodName + " body=", body);
		// 增加实现接口的方法
		CtMethod method = newInterface.getDeclaredMethod(methodName);
		CtMethod newMethod = CtNewMethod.make(method.getReturnType(),
				methodName, method.getParameterTypes(), method
						.getExceptionTypes(), "{" + body + "}", newCtClass);
		newCtClass.addMethod(newMethod);
	}
	
	private void addMethod(CtClass newCtClass, NewMethod newMethod) throws CannotCompileException,
			NotFoundException {
		String name = newMethod.getName();
		String body = newMethod.getBody();
		LogWriter.debug(logger, name + " body=", body);
//		转换方法的输入参数
		CtClass[] paramTypes = null; 
		if (newMethod.getParamsType() != null) {
			paramTypes = new CtClass[newMethod.getParamsType().length];
			for (int i = 0; i < paramTypes.length; i++) {
				paramTypes[i] = pool.get(newMethod.getParamsType()[i].getName());
			}
		}
		
		//转换方法的返回类型
		CtClass returnType = CtClass.voidType; 
		if (newMethod.getReturnType() != null && newMethod.getReturnType() != Void.class) {
			returnType = pool.get(newMethod.getReturnType().getName());
		}
		
//		转换方法的抛出异常
		CtClass[] expTypes = null; 
		if (newMethod.getExceptionsType() != null) {
			expTypes = new CtClass[newMethod.getExceptionsType().length];
			for (int i = 0; i < expTypes.length; i++) {
				expTypes[i] = pool.get(newMethod.getExceptionsType()[i].getName());
			}
		}
		
		// 增加实现接口的方法
		CtMethod newCtMethod = CtNewMethod.make(returnType,
				name, paramTypes, expTypes, "{" + body + "}", newCtClass);
		newCtClass.addMethod(newCtMethod);
	}
}

class Key {
	/**
	 * 原始的、被修改的类
	 */
	private final Class origianlClass;

	/**
	 * 增加的接口
	 */
	private final Class interfaceClass;

	public Key(Class origianlClass, Class interfaceClass) {
		this.origianlClass = origianlClass;
		this.interfaceClass = interfaceClass;
	}

	/**
	 * 一般情况下，bizHadler和method是不能为空的
	 * 
	 * @param pp
	 * @return 是否相等
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) {
			return false;
		}
		Key other = (Key) obj;
		return (origianlClass == other.origianlClass && interfaceClass == other.interfaceClass);
	}

	public int hashCode() {
		int code = 0;
		if (origianlClass != null) {
			code = code + origianlClass.hashCode();
		}
		if (interfaceClass != null) {
			code = code + interfaceClass.hashCode();
		}
		return code;
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		if (origianlClass != null) {
			strBuf.append(origianlClass.toString());
		}
		strBuf.append("@");
		if (interfaceClass != null) {
			strBuf.append(interfaceClass.toString());
		}
		return strBuf.toString();
	}

}
