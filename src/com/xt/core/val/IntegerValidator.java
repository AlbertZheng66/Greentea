package com.xt.core.val;

public class IntegerValidator implements IValidator {

	private int maxLength = 0;

	private int minLength;

	private String value;

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}只包含数字," + "且不能以'0'开头");

		errMsg.append(",其值只能要大于等于").append(minLength).append(",小于等于").append(
				maxLength);

		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		// 校验表达式是否合法
		if (!value.matches("INTEGER(\\{[\\-]?\\d+\\,\\d+\\})?")) {
			throw new ValidatorException("表达式[" + value
					+ "]不符合ID表达式[INTEGER\\({[\\-+]?\\d+\\,\\d+\\})?]！");
		}

		int length = "INTEGER".length();
		// 解析得出加大长度和最小长度
		String content = value.substring(length + 1, value.length() - 1); // 去掉INTEGER{和}
		String[] segs = content.split(","); // 分为最大值和最小值
		minLength = Integer.parseInt(segs[0]);
		maxLength = Integer.parseInt(segs[1]);
		
		//首先判断是否是整数
		

		return value.replaceFirst("INTEGER", "range");
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId () {
		return "int";
	}

}