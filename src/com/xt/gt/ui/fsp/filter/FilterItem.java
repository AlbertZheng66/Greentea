package com.xt.gt.ui.fsp.filter;

import java.io.Serializable;

/**
* 用于封装过滤选项。过滤选项可以是一个简单的相等表达式，也可以是带有And或者Or的复杂的表达式。
* @author albert
*/
public interface FilterItem extends Cloneable, Serializable{
	
	/**
	 * 过滤选项的名称，可以代表属性名、字段名等信息。
	 * @return
	 */
	public String getName();
	
	/**
	 * 设置QL语句构建器
	 * @param nameConvertor
	 */
	public void setQLBuilder(QLBuilder qlBuilder);
	
	/**
	 * 设置“非”运算
	 * @param not
	 */
	public void setNot (boolean not);
	
	/**
	 * 返回参数的名称
	 * @return
	 */
	public String toQlString(String alias);
	
	/**
	 * 返回QL 语句的参数。
	 * @return
	 */
	public Object[] getParameters ();
	
	
	/**
	 * 此对象是否满足本过滤选项。
	 * @param obj
	 * @return
	 */
	public boolean match (Object obj);
	
	/**
	 * 判断两个过滤选项是否恒等。一般情况下，两个表达式的名称和类型（指：大于、小于等比较表达式）分别相等
	 * ，则认为这两个过滤项相等。如果两个表达式的值亦对应相等，则表示这两个表达式相等。
	 */
	public boolean isIdentical(FilterItem otherItem);
	
	
	/**
	 * 过滤选项必须可以复制本身。
	 * @return
	 */
	public Object clone();

}
