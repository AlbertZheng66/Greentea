package com.xt.views.taglib.html;

import java.text.ParseException;
import java.util.Calendar;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.DateUtils;
import com.xt.gt.sys.SystemConfiguration;

/**
 * <p>
 * Title: 自定义日期标签
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004年2月14日
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public class DateTag extends TextTag {

	private static final long serialVersionUID = 2995964144949885590L;

	/**
	 * 只显示月份
	 */
	private boolean onlyMonth = false;

	/**
	 * 只显示日期
	 */
	private boolean onlyDate = true;

	/**
	 * 只显示时间
	 */
	private boolean showTime = false;

	/**
	 * 日期控件的图标
	 */
	private String dateImage = "/images/date.gif";

	/**
	 * // * 时间控件的图标 //
	 */
	// private String timeImage;
	//
	// /**
	// * 月份控件的图标
	// */
	// private String monthImage;
	/**
	 * 日期的缺省显示格式
	 */
	private static String dateFormat;

	/**
	 * 时间的缺省显示格式
	 */
	private static String timeFormat;

	/**
	 * 月份的缺省显示格式
	 */
	private static String monthFormat;

	/**
	 * 日期的缺省校验格式
	 */
	private static String defaultValidator;

	private String dateStatusFunc;

	private String onUpdate;

	static {
		dateFormat = SystemConfiguration.getInstance().readString("dateFormat",
				"yyyy-MM-dd");

		timeFormat = SystemConfiguration.getInstance().readString("timeFormat",
				"yyyy-MM-dd hh:mm:ss");

		monthFormat = SystemConfiguration.getInstance().readString(
				"monthFormat", "yyyy-MM");
	}

	/**
	 * 日期缺省值，缺省为当前日期。
	 */
	private String defaultValue;

	/**
	 * JavaScript的日期格式
	 */
	private String jsFormater;

	/**
	 * 输出的日期值
	 */
	private Calendar dateValue;

	public DateTag() {
		super();
		this.type = "text";

		this.validator = defaultValidator;
	}

	public int doStartTag() throws JspException {
		if (onlyMonth) {
			formater = monthFormat;
		} else if (showTime) {
			formater = timeFormat;
		} else {
			formater = dateFormat;
		}
		defaultValidator = "DATE{" + formater + "}";
		maxlength = String.valueOf(formater.length());

		// 将Java的日期格式转换为JavaScript格式
		jsFormater = formater.replaceAll("yy*+", "%Y");
		jsFormater = jsFormater.replaceAll("MM", "%m");
		jsFormater = jsFormater.replaceAll("dd", "%d");
		jsFormater = jsFormater.replaceAll("hh", "%H");
		jsFormater = jsFormater.replaceAll("mm", "%M");
		jsFormater = jsFormater.replaceAll("ss", "%S");

		// 首先计算父类的标签（获取原始值）
		try {
			Object obj = TagUtils.getInstance().lookup(pageContext, name,
					property, null);
			if (obj != null && obj instanceof Calendar) {
				dateValue = (Calendar) obj;
			}
		} catch (Throwable e) {
			// 如果未找到指定值，则不进行处理
			LogWriter.warn(e.getMessage());
		}

		if (dateValue == null) {
			if (defaultValue == null) {
				dateValue = Calendar.getInstance();
			} else {
				Calendar cal = Calendar.getInstance();
				try {
					cal.setTime(DateUtils.parseDate(defaultValue, formater));
				} catch (ParseException e) {
					throw new JspException("缺省值[" + defaultValue + "]和格式["
							+ formater + "]不相匹配！");
				}
				dateValue = cal;
			}
		}

		LogWriter.debug("doStartTag dateValue=", dateValue);
		return super.doStartTag();
	}

	protected void prepareValue(StringBuffer str) {
		str.append("value='").append(DateUtils.toDateStr(dateValue, formater))
				.append("'");
	}

	protected String renderInputElement() throws JspException {
		// 如果没有定义输入域的ID，则默认生成一个，便于JS回填

		String inputId = "inputId_" + this.property;
		setStyleId(inputId);

		StringBuilder strBuf = new StringBuilder(super.renderInputElement());

		// 生成按钮的默认ID，便于JavaScript注册事件
		String buttonId = "tbid_" + this.property;

		// 在输入域后面增加一个日期按钮标签
		strBuf.append("<input type='button'  class='dateButton'");
		strBuf.append(" value='&nbsp;&nbsp;&nbsp;' ");
		strBuf.append(" id='").append(buttonId).append("'>");

		strBuf.append("<script type='text/javascript'>\n");

		// 插入开始或者结束日期的初始值
		LogWriter.debug("dateStatusFunc=", dateStatusFunc);
		/**
		 * @FIXME 这段代码太依赖于初始函数了！！！
		 */
		if (dateStatusFunc != null) {
			strBuf.append("initDate('").append(dateStatusFunc).append("', ")
					.append(dateValue.get(Calendar.YEAR)).append(", ").append(
							dateValue.get(Calendar.MONTH)).append(", ").append(
							dateValue.get(Calendar.DATE)).append(");\n");
		}

		strBuf.append("	 var cal = new Zapatec.Calendar.setup({\n");
		strBuf.append("	 inputField : '").append(inputId).append("',\n");
		strBuf.append("	 ifFormat   : '").append(jsFormater).append("',\n");
		if (showTime) {
			strBuf.append("showsTime : true, ");
			strBuf.append("	 timeFormat : '24',\n");
		}
		strBuf.append("	 button     : '").append(buttonId).append("', \n");
		strBuf.append("	 dateStatusFunc : ").append(dateStatusFunc).append(
				", \n");
		strBuf.append("	 onUpdate     : ").append(onUpdate).append(" \n");
		strBuf.append("})");
		strBuf.append("</script>\n");

		return strBuf.toString();
	}

	public void release() {
		onlyMonth = false;
		onlyDate = true;
		showTime = false;
		dateImage = "/images/date.gif";
		defaultValidator = null;
		dateStatusFunc = null;
		onUpdate = null;
		super.release();
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isOnlyDate() {
		return onlyDate;
	}

	public void setOnlyDate(boolean onlyDate) {
		this.onlyDate = onlyDate;
	}

	public boolean isOnlyMonth() {
		return onlyMonth;
	}

	public void setOnlyMonth(boolean onlyMonth) {
		this.onlyMonth = onlyMonth;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public String getDateStatusFunc() {
		return dateStatusFunc;
	}

	public void setDateStatusFunc(String dateStatusFunc) {
		this.dateStatusFunc = dateStatusFunc;
	}

	public String getOnUpdate() {
		return onUpdate;
	}

	public void setOnUpdate(String onUpdate) {
		this.onUpdate = onUpdate;
	}

}
