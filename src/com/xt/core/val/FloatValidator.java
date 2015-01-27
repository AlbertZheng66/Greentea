package com.xt.core.val;

public class FloatValidator implements IValidator {

	private int intLength = 10; // 整数位的长度

	private int decLength = 2; // 小数位的长度

	private String name;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}只包含数字" + "");
		// 用户定义了最大和最小长度
		if (intLength > 0) {
			errMsg.append(",且整数位长度只能要小于等于").append(intLength).append(
					",小数部分小于等于").append(decLength);
		}

		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		// 校验表达式是否合法
		if (!name.matches("FLOAT(\\{\\d+\\,\\d+\\})?")) {
			throw new ValidatorException("表达式[" + name
					+ "]不符合FLOAT表达式[FLOAT\\({\\d+\\,\\d+\\})?]！");
		}
		
		int length = "FLOAT".length();
		if (name.length() > length) {
			// 解析得出加大长度和最小长度
			String content = name.substring(length + 1,
					name.length() - 1); // 去掉ID{和}
			String[] segs = content.split(","); // 分为最大值和最小值
			intLength = Integer.parseInt(segs[0]);
			decLength = Integer.parseInt(segs[1]);
		}
		StringBuilder regExp = new StringBuilder("^\\d{1,");
		regExp.append(intLength);
		regExp.append("}(\\.\\d{1,").append(decLength).append("})?");
		regExp.append("$");
		return regExp.toString();
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "float";
	}

}
