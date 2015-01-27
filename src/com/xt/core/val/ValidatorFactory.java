package com.xt.core.val;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.SystemException;
import com.xt.core.utils.ClassHelper;

/**
 * 校验器工厂。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 校验器工厂用于注册和返回相应的校验器。
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
 * @date 2006-9-16
 */
public class ValidatorFactory {

	private static Map<String, Class<? extends IValidator>> validators = new Hashtable<String, Class<? extends IValidator>>();
	
	static {
		register("id",       IDValidator.class);
		register("int",      IntegerValidator.class);
		register("http",     HttpValidator.class);
		register("float",    FloatValidator.class);
		register("email",    EmailValidator.class);
		register("length",   LengthValidator.class);
		register("mobil",    MobilValidator.class);
		register("phone",    PhoneNumberValidator.class);
		register("text",     TextValidator.class);
		register("digital",  DigitalValidator.class);
		register("date",     DateValidator.class);
		register("required", RequiredValidator.class);
	}

	public ValidatorFactory() {
	}

	/**
	 * 注册校验器，在使用某个程序之前要先注册校验器，一般在程序启动时先将其加载。
	 * 
	 * @param prefix
	 *            校验器前缀
	 * @param clazz
	 *            校验器处理类
	 */
	public static <T extends IValidator> void register(String prefix, Class<T> clazz) {
		if (StringUtils.isBlank(prefix)) {
			throw new SystemException("校验器的名称不能为空！");
		}

		if (null == clazz) {
			throw new SystemException("校验器实现类不能为空！");
		}
		validators.put(prefix, clazz);
	}

	/**
	 * 多个校验器之间以“;”为分割符，如果分隔符之间为空白，则忽略这个校验器。
	 * 
	 * @param names
	 * @return 校验器数组
	 */
	public IValidator[] getValidators(String names) {
		List<IValidator> valList = new ArrayList<IValidator>(2);
		String[] nameArray = names.split(";"); // 多个校验器之间以“;”为分割符
		for (int i = 0; i < nameArray.length; i++) {
			String name = nameArray[i];
			if (StringUtils.isNotBlank(name)) {
				valList.add(getValidator(name));
			}
		}
		return (IValidator[]) valList.toArray(new IValidator[valList.size()]);
	}

	/**
	 * 注册校验器，在使用某个程序之前要先注册校验器，一般在程序启动时先将其加载。
	 * 
	 * @param prefix
	 *            校验器前缀
	 * @exception 如果名称为空，或者为空白，或者为发现相应的校验器，
	 *                则抛出SystemException异常；
	 */
	public IValidator getValidator(String name) {
		// 先解析得到注册的名称
		String realName = parseName(name);

		Class<? extends IValidator> valClass = validators.get(realName);
		if (valClass == null) {
			throw new SystemException("未发现校验器[" + realName + "]！");
		}
		IValidator validator = (IValidator) ClassHelper.newInstance(valClass);
		validator.setValue(name);
		return validator;
	}

	/**
	 * 解析表达式的名称，如果表达式的名称后面还有表达式变量，则将其解析出去。
	 * 
	 * @param fullName
	 * @return
	 */
	private String parseName(String fullName) {
		if (StringUtils.isBlank(fullName)) {
			throw new SystemException("校验器的名称不能为空！");
		}

		// 只作简单处理，看到“{”就将其后部截掉
		int index = fullName.indexOf("{");
		if (index > -1) {
			return fullName.substring(0, index);
		}
		return fullName;
	}

}
