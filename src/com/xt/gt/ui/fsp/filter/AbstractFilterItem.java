package com.xt.gt.ui.fsp.filter;

import com.xt.core.exception.SystemException;
import com.xt.gt.sys.SystemConfiguration;

abstract public class AbstractFilterItem implements FilterItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3164387703833393388L;

	/**
	 * 系统参数：默认的QL语言创建器。
	 */
	private static final String DEFAULT_QL_BUILDER = "defaultQlBuilder";
	

	protected QLBuilder qlBuilder = (QLBuilder)SystemConfiguration.getInstance().readObject(DEFAULT_QL_BUILDER, new IPOQLBuilder());

	/**
	 * 非运算（默认为：false）
	 */
	protected boolean not;

	public AbstractFilterItem() {
        
	}

	/**
	 * 设置“非”运算
	 */
	public void setNot(boolean not) {
		this.not = not;
	}
	
	public void setQLBuilder(QLBuilder qlBuilder) {
		this.qlBuilder = qlBuilder;
	}
	
	public String toQlString(String alias) {
		return qlBuilder.getQLString(this, alias);
	}


	public Object[] getParameters() {
		return qlBuilder.getParams(this);
	}
	
	abstract public String getName();
	
    public Object clone() {
    	try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new SystemException("克隆过滤条件时出现错误！", e);
		}
    }
	
}
