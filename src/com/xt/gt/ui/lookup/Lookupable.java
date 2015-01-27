
package com.xt.gt.ui.lookup;

/**
 * 查找接口，自定义的查找实现类必须实现此接口。
 * @author albert
 */
public interface Lookupable {

    public void setPropertyName (String propertyName);

    public String getPropertyName();

    /**
     * 设置用户自定义的参数
     */
    public void setParams(String params);

    /**
     * 当前数据选中的值
     */
    public void setSelectedValue(Object value);

    /**
     * 返回查找的结果
     * @return
     */
    public Object getResult();

    /**
     * 对用户手工输入的值进行校验
     * @param value
     * @return
     */
    public boolean validate(Object value);

    /**
     * 多数情况下，关联 Lookup 的类只保留了Lookup实现类的一行的某个属性（即主键），
     * 但是显示的时候可能会用到整个对象的信息，此时（包括初始化时）需要使用此方法得到
     * 完整的对象。
     * @param value
     * @return
     */
    public Object getActualValue(Object value);
}
