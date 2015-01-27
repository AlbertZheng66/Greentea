package com.xt.core.val;

/**
 * 用于对一个输入域的输入的字符串长度进行判断。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2007-2-15
 */
public class LengthValidator implements IValidator {

	private int maxLength = 0;

	private int minLength;

	private String name;
	
	public LengthValidator () {
		
	}

	public String getErrorMessage() {
		StringBuilder errMsg = new StringBuilder("{title}的长度只能要大于等于");
		errMsg.append(minLength).append(",小于等于").append(maxLength).append("。");
		return errMsg.toString();
	}

	public String getRegExpStr(int type) throws ValidatorException {
		// 校验表达式是否合法
		if (!name.matches("LENGTH(\\{\\d+\\,\\d+\\})?")) {
			throw new ValidatorException("表达式[" + name
					+ "]不符合LENGTH表达式[ID\\({\\d+\\,\\d+\\})?]！");
		}
		StringBuilder regExp = new StringBuilder("^.");

		// 大于参数的情况
		int length = "LENGTH".length();
		// 追加度和最小长度
		regExp.append(name.substring(length));
		
		// 解析得出加大长度和最小长度
		String content = name.substring(length + 1, name.length() - 1); // 去掉LENGTH{和}
		String[] segs = content.split(","); // 分为最大值和最小值
		minLength = Integer.parseInt(segs[0]);
		maxLength = Integer.parseInt(segs[1]);
			
		regExp.append("$");
		return regExp.toString();
	}

	public void setValue(String value) {
		this.name = value;
	}

	public String getId () {
		return "length";
	}

}
