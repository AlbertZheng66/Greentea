package com.xt.core.utils;

import java.util.Calendar;

import org.apache.commons.beanutils.Converter;

import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 日期（Calendar）类型转换器。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 将字符串类型（按照系统定义的日期格式）或者长整型
 * （表示1970至今的毫秒数）转换为日期类型。如果不是这些类型，则返回空。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-10
 */
public class CalendarConverter implements Converter {
	
	private static String dateFormat = null;

	static {
		dateFormat = SystemConfiguration.getInstance().readString(
				SystemConstants.DATE_FORMAT, "yyyy-MM-dd");
	}

	public Object convert(Class arg0, Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Calendar) {
			return (value);
		} else if (value instanceof String) {
			if (!org.apache.commons.lang.StringUtils.isBlank((String) value)) {
				return DateUtils.parseCalendar((String) value, dateFormat);
			}
		} else if (value instanceof Long) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(((Long) value).longValue());
		}
		return null;
	}

}
