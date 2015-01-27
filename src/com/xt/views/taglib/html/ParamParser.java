package com.xt.views.taglib.html;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.SystemException;

public class ParamParser {
	
	private String name;
	
	private String params;
	
	public ParamParser() {
	}
	
	/**
	 * 解析参数，参数的形式为：name{param}；名称部分是必须的，参数部分，包括大括号“{}”是可选的
	 * @param param 如果参数为空，抛出SystemException异常
	 * @return
	 */
	public static ParamParser getInstance(String param) {
		ParamParser pp = new ParamParser();
		String name = parseName(param);
		pp.setName(name);
		if (name.length() < param.length()) {
			pp.setParams(param.substring(name.length() + 1, param.length() - 1));
		}
		return pp;
	}
	
	/**
	 * 解析表达式的名称，如果表达式的名称后面还有表达式变量，则将其解析出去。
	 * 
	 * @param fullName
	 * @return
	 */
	private static String parseName(String param) {
		if (StringUtils.isBlank(param)) {
			throw new SystemException("域控制器参数不能为空！");
		}

		// 只作简单处理，看到“{”就将其后部截掉，否则返回整个字符串
		int index = param.indexOf("{");
		if (index > -1) {
			return param.substring(0, index);
		}
		return param;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
}

