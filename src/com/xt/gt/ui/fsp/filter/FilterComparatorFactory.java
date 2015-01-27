package com.xt.gt.ui.fsp.filter;

import java.util.Hashtable;
import java.util.Map;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 过滤比较器工厂。
 * 
 * @author albert
 * 
 */
public class FilterComparatorFactory {

	private final Map<String, FilterComparator> comparators = new Hashtable<String, FilterComparator>();

	public static FilterComparatorFactory instance = new FilterComparatorFactory();

	private FilterComparatorFactory() {
		// 默认的初始化加载
		comparators.put(FilterType.EQUALS.name(), new EqualsComparator());
		comparators.put(FilterType.STARTS_WITH.name(),
				new StartsWithComparator());
		comparators.put(FilterType.ENDS_WITH.name(), new EndsWithComparator());
		comparators.put(FilterType.CONTAINS.name(), new ContainsComparator());
		comparators
				.put(FilterType.GREAT_THAN.name(), new GreatThanComparator());
		comparators.put(FilterType.GREAT_EQUAL_THAN.name(),
				new GreatEqualThanComparator());
		comparators.put(FilterType.LESS_THAN.name(), new LessThanComparator());
		comparators.put(FilterType.LESS_EQUAL_THAN.name(),
				new LessEqualThanComparator());
		comparators.put(FilterType.IS_EMPTY.name(), new EmptyComparator());
		comparators.put(FilterType.IS_NULL.name(), new IsNullComparator());
		comparators.put(FilterType.IN.name(), new InComparator());
		comparators.put(FilterType.BETWEEN.name(), new BetweenComparator());
	}

	public static FilterComparatorFactory getInstance() {
		return instance;
	}

	synchronized public void register(FilterType type,
			FilterComparator comparator) {
		if (type != null && comparator != null) {
			this.comparators.put(type.name(), comparator);
		}
	}

	synchronized public void register(String name, FilterComparator comparator) {
		if (name != null && comparator != null) {
			this.comparators.put(name, comparator);
		}
	}

	public FilterComparator getFilterComparator(FilterType type) {
		if (type == null) {
			return null;
		}
		return this.comparators.get(type.name());
	}

	public FilterComparator getFilterComparator(String name) {
		if (name == null) {
			return null;
		}
		return this.comparators.get(name);
	}
	
	/**
	 * 判断一个参考值和匹配对象地方是为空相等。如果匹配对象为空，参考值为空，或者其长度为0，或者第一个值为空，返回 true；
	 * 否则，返回 false。
	 * @param matchedObject  匹配对象
	 * @param referenceValues 参考值
	 * @return 
	 */
	private boolean isNull(Object matchedObject, Object[] referenceValues) {
		return (matchedObject == null || referenceValues == null
				|| referenceValues.length == 0 || referenceValues[0] == null);
	}
	
/**
 * 如果匹配对象为空，则返回 true，否则返回 false。
 * @author albert
 *
 */
	class IsNullComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] values) {
			return (matchedObject == null);
		}

	}
	
	@SuppressWarnings("unchecked")
	private Object getReferenceValue(Class matchedClazz, Object referenceValue) {
		if (matchedClazz.isAssignableFrom(referenceValue.getClass())) {
			return referenceValue;
		}
		Converter converter = ConverterFactory.getInstance().getConverter(matchedClazz, referenceValue.getClass());
		return converter.convert(matchedClazz, referenceValue.getClass(), referenceValue);
	}

	class EqualsComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (matchedObject == null) {
				return (referenceValues != null && referenceValues.length > 0);
			}
			if (referenceValues == null || referenceValues.length < 1) {
				return false;
			}
			Object referenceValue = getReferenceValue(matchedObject.getClass(), referenceValues[0]);
			// 对于 double 等设计到精度的比较需要另行处理
			if (referenceValue instanceof Double && matchedObject instanceof Double) {
				Double valueDbl = (Double) referenceValue;
				Double objDbl = (Double) matchedObject;
				return Math.abs(valueDbl.doubleValue() - objDbl.doubleValue()) < SystemConfiguration
						.getInstance().readDouble(
								SystemConstants.NUMBER_COMPARATOR_PRECISION,
								0.0000001);
			}
			if (referenceValue instanceof Float && matchedObject instanceof Float) {
				Float valueDbl = (Float) referenceValue;
				Float matchedObjectDbl = (Float) matchedObject;
				return Math.abs(valueDbl.floatValue()
						- matchedObjectDbl.floatValue()) < SystemConfiguration
						.getInstance().readDouble(
								SystemConstants.NUMBER_COMPARATOR_PRECISION,
								0.0000001);
			}
			return matchedObject.equals(referenceValue);
		}

	}

	class EndsWithComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = referenceValues[0];
			String strReferenceValue = referenceValue.toString();
			String strMatchedObject = matchedObject.toString();
			return strMatchedObject.endsWith(strReferenceValue);
		}

	}

	class StartsWithComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = referenceValues[0];
			String strReferenceValue = referenceValue.toString();
			String strMatchedObject = matchedObject.toString();
			return strMatchedObject.startsWith(strReferenceValue);
		}

	}

	class ContainsComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = referenceValues[0];
			String strReferenceValue = referenceValue.toString();
			String strMatchedObject = matchedObject.toString();
			return strMatchedObject.contains(strReferenceValue);
		}

	}

	class EmptyComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (matchedObject == null) {
				return true;
			}
			String strMatchedObject = matchedObject.toString();
			return (strMatchedObject.length() == 0);
		}

	}

	class GreatThanComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = getReferenceValue(matchedObject.getClass(), referenceValues[0]);
			if (matchedObject instanceof Comparable) {
				return ((Comparable) matchedObject).compareTo(referenceValue) > 0;
			}
			return false;
		}
	}

	class GreatEqualThanComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = getReferenceValue(matchedObject.getClass(), referenceValues[0]);
			if (matchedObject instanceof Comparable) {
				return ((Comparable) matchedObject).compareTo(referenceValue) >= 0;
			}
			return false;
		}

	}

	class LessThanComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = getReferenceValue(matchedObject.getClass(), referenceValues[0]);
			if (matchedObject instanceof Comparable) {
				return ((Comparable) matchedObject).compareTo(referenceValue) < 0;
			}
			return false;
		}

	}

	class LessEqualThanComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			Object referenceValue = getReferenceValue(matchedObject.getClass(), referenceValues[0]);
			if (matchedObject instanceof Comparable) {
				return ((Comparable) matchedObject).compareTo(referenceValue) <= 0;
			}
			return false;
		}

	}

	class InComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)) {
				return false;
			}
			for (int i = 0; i < referenceValues.length; i++) {
				if (matchedObject.equals(getReferenceValue(matchedObject.getClass(), referenceValues[i]))) {
					return true;
				}
			}
			return false;
		}

	}

	class BetweenComparator implements FilterComparator {

		public boolean match(Object matchedObject, Object[] referenceValues) {
			if (isNull(matchedObject, referenceValues)
					|| referenceValues.length < 2) {
				return false;
			}
			FilterComparator begin = new GreatEqualThanComparator();
			FilterComparator end = new LessEqualThanComparator();
			return (begin.match(matchedObject, new Object[] { getReferenceValue(matchedObject.getClass(), referenceValues[0]) }) && end
					.match(matchedObject, new Object[] { getReferenceValue(matchedObject.getClass(), referenceValues[1]) }));
		}

	}


}
