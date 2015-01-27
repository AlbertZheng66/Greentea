package com.xt.core.utils.formater;

import java.util.Calendar;

import com.xt.core.utils.DateUtils;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

public class CalendarFormater implements Formater {

	private static String defaultFormater = SystemConfiguration.getInstance()
			.readString(SystemConstants.DATE_FORMAT, "yyyy-MM-dd");

	public CalendarFormater() {
	}

	/**
	 * 格式化一个日期类型的变量。如果被格式化的参数为空或者非日期类型，则返回空；
	 * 如果日期的模式为空，或者为DEFAULT，则采用系统默认的字符串。
	 */
	public String format(Object obj, String dateFormat) {
		if (obj != null && obj instanceof Calendar) {
			dateFormat = (dateFormat == null || "DEFAULT".equalsIgnoreCase(dateFormat)) 
			             ? defaultFormater : dateFormat;
			Calendar cal = (Calendar) obj;
			return DateUtils.toDateStr(cal, dateFormat);
		}
		return null;
	}

}
