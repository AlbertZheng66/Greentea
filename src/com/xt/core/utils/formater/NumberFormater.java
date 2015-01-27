package com.xt.core.utils.formater;

import java.text.DecimalFormat;

import com.xt.gt.sys.SystemConfiguration;

public class NumberFormater implements Formater {

	private static String defaultFloatFormater = SystemConfiguration
			.getInstance().readString("FLOAT_FORMATER", "###.00");

	private static String defaultIntegerFormater = SystemConfiguration
			.getInstance().readString("FLOAT_FORMATER", "###");

	private static final double DEFAULT_PRISION = 0.000001;

	public NumberFormater() {
	}

	/**
	 *  是否显示0值。当没有自定义formater时，一般认为是要求输入域的情况，不显示“0”
	 *  值；否则，假设是显示某个数值的情况，显示“0”值。�
	 */
	public String format(Object obj, String numberFormat) {
		boolean showZero = true;

		if (obj != null) {
			if (obj instanceof Float) {
				if (numberFormat == null
						|| "DEFAULT".equalsIgnoreCase(numberFormat)) {
					numberFormat = defaultFloatFormater;
					showZero = false;
				}
				DecimalFormat df = new DecimalFormat(numberFormat);
				Float f = (Float) obj;
				if (!showZero && Math.abs(f.floatValue()) < DEFAULT_PRISION) {
					return "";
				}
				return df.format(f.floatValue());
			} else if (obj instanceof Double) {
				if (numberFormat == null
						|| "DEFAULT".equalsIgnoreCase(numberFormat)) {
					numberFormat = defaultFloatFormater;
					showZero = false;
				}
				DecimalFormat df = new DecimalFormat(numberFormat);
				Double d = (Double) obj;
				if (!showZero && Math.abs(d.doubleValue()) < DEFAULT_PRISION) {
					return "";
				}
				return df.format(d.doubleValue());
			} else if (obj instanceof Integer) {
				if (numberFormat == null
						|| "DEFAULT".equalsIgnoreCase(numberFormat)) {
					numberFormat = defaultIntegerFormater;
					showZero = false;
				}
				DecimalFormat df = new DecimalFormat(numberFormat);

				Integer i = (Integer) obj;
				if (!showZero && (i.intValue() == 0)) {
					return "";
				}
				return df.format(i.intValue());
			} else if (obj instanceof Long) {
				if (numberFormat == null
						|| "DEFAULT".equalsIgnoreCase(numberFormat)) {
					numberFormat = defaultIntegerFormater;
					showZero = false;
				}
				DecimalFormat df = new DecimalFormat(numberFormat);
				Long l = (Long) obj;
				if (!showZero && (l.longValue() == 0)) {
					return "";
				}
				return df.format(l.longValue());
			}
			return "";
		}
		return null;
	}

}
