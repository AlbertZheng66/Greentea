package com.xt.gt.ui.fsp;

import java.io.Serializable;

import com.xt.core.exception.SystemException;


/**
 * 用于定义排序相关信息，如：排序的名称、排序类型。
 * @author albert
 */
public class SortItem implements Cloneable, Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 436037442830702874L;

	/**
	 * 排序名称，用于唯一区别排序选项，可自行定义。
	 */
    private final String name;
    
    /**
     * 排序类型。默认为升序。
     */
    private SortType type = SortType.ASC;

    public SortItem(String name) {
        this.name = name;
    }
    
    public SortItem(String name, SortType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }
    

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortItem other = (SortItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		strBld.append(super.toString()).append("[");
		strBld.append("name=").append(name).append(",");
		strBld.append("type=").append(type).append("]");
		return strBld.toString();
	}

	public SortType getType() {
        return type;
    }

    public void setType(SortType type) {
        this.type = type;
    }
    
    public Object clone() {
    	try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new SystemException("克隆排序对象时产生异常�?", e);
		}
    }
    
}
