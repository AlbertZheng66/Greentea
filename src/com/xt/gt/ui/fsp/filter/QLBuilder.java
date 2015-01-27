package com.xt.gt.ui.fsp.filter;

import java.io.Serializable;

/**
 * 通过此接口进行QL（SQL、HQL等）语句的转换。
 * @author albert
 *
 */
public interface QLBuilder extends Serializable {
	
	/**
	 * 返回合法的 QL 语句。
	 * @param filterItem，不为空。
	 * @return 合法的 QL 语句
	 */
	public String getQLString (FilterItem filterItem, String alias);
	
	/**
	 * 
	 * @param filterItem
	 * @return
	 */
	public Object[] getParams (FilterItem filterItem);

}
