package com.xt.views.taglib.html;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

/**
 * 用于录入生日日期的标签
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2007-3-28
 */
public class BirthdayTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7486618338096363801L;

	/**
	 * 起始年份，默认是：1930
	 */
	private int startFrom = 1930;

	/**
	 * 结束年份（默认是小于当前时间10年）
	 */
	private int endYear = 10;

	/**
	 * 默认的当前年龄（最多的人群）
	 */
	private int ages = 23;

	/**
	 * 欲赋值的属性
	 */
	private String property;

	public BirthdayTag() {
	}

	/**
	 * 错误消息优先
	 */
	public int doStartTag() throws JspException {
		StringBuilder strBld = new StringBuilder();

		// 创建年
		createSelectStart(strBld, "year", "onBirthdayChange");
		Calendar cal = Calendar.getInstance();
		createOption(strBld, startFrom, cal.get(Calendar.YEAR) - endYear, 4,
				ages);
		createSelectEnd(strBld);

		// 创建月
		createSelectStart(strBld, "month", "onBirthdayChange");
		createOption(strBld, 1, 13, 2, 1);
		createSelectEnd(strBld);

		// 创建日
		createSelectStart(strBld, "date", "onBirthdayChange");
		createOption(strBld, 1, 32, 2, 1);
		createSelectEnd(strBld);

		// 创建隐藏域(真实的提交值)
		strBld.append("<input name='").append(property).append(
				"' value='' type='hidden'>");

		try {
			pageContext.getOut().write(strBld.toString());
		} catch (IOException e) {
			throw new JspException("标签的IO操作出现异常", e);
		}

		return (SKIP_BODY);

	}

	private void createSelectStart(StringBuilder strBld, String name,
			String onChange) {
		strBld.append("<select size='1' ");
		if (onChange != null) {
			strBld.append(" onchange='").append(onChange).append("(this, \"")
					.append(property).append("\")'");
		}
		strBld.append(" id='_").append(property).append('_').append(name)
				.append("' ");
		strBld.append(" name='_").append(property).append('_').append(name)
		.append("' ");
		strBld.append(">");
	}

	private void createSelectEnd(StringBuilder strBld) {
		strBld.append("</select>");
	}

	private void createOption(StringBuilder strBld, int start, int end,
			int length, int selected) {
		for (int i = start; i < end; i++) {
			String value = StringUtils.leftPad(String.valueOf(i), length, '0');
			strBld.append("<option value='").append(value).append("' ");
			if (i == selected) {
				strBld.append("selected='true'");
			}
			strBld.append(">").append(value).append("</option>");
		}

	}

	public int getAges() {
		return ages;
	}

	public void setAges(int ages) {
		this.ages = ages;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public int getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(int startFrom) {
		this.startFrom = startFrom;
	}

}
