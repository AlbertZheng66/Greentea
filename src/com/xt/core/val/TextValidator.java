package com.xt.core.val;

/**
 * 文本校验器
 * <p>
 * Title: XT框架-核心逻辑部分
 * </p>
 * <p>
 * Description: 这里的ID定义为只包含字母，数字和下划线的字符串。 
 * 这个校验器可以带有最大和最小长度参数，以{min,max}的形式出现。
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
public class TextValidator implements IValidator {

	private int maxLength = 0;

	private int minLength;

	private String name;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}只包含文本");
		// 用户定义了最大和最小长度
		if (maxLength > 0) {
			errMsg.append(",且长度只能要大于等于").append(minLength).append(",小于等于")
					.append(maxLength);
		}

		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		// 校验表达式是否合法
		if (!name.matches("TEXT(\\{\\d+\\,\\d+\\})?")) {
			throw new ValidatorException("表达式[" + name
					+ "]不符合TEXT表达式[TEXT\\({\\d+\\,\\d+\\})?]！");
		}
		StringBuilder regExp = new StringBuilder("^.");

		// 大于参数的情况
		int length = "TEXT".length();
		if (name.length() > length) {
			regExp.append(name.substring(length).trim());
			// 解析得出加大长度和最小长度
			String content = name.substring(length + 1,
					name.length() - 1); // 去掉TEXT{和}
			String[] segs = content.split(","); // 分为最大值和最小值
			minLength = Integer.parseInt(segs[0]);
			maxLength = Integer.parseInt(segs[1]);
		} else {
			regExp.append("*");
		}
		regExp.append("$");
		return regExp.toString();
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "text";
	}

}
