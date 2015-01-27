package com.xt.gt.jt.proc.result.ajax;

public interface ToAjax {
	
	/**
	 * 增加节点的属性
	 */
    public void appendAttributes (StringBuilder strBld, Object value);
    
	/**
	 * 增加节点的子节点
	 */
    public void appendChildren (StringBuilder strBld, Object value);
}
