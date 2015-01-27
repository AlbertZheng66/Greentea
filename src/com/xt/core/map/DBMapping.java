package com.xt.core.map;


/**
 * <p>Title: XT框架-持久化部分－数据库映射.</p>
 * <p>Description: 这个接口是数据库映射接口，将数据库中的表和
 * 字段与PO对象和属性，分别建立起一一对应关系。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface DBMapping
{
	/**
	 * 读取指定类的属性所对应的数据库字段。
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
    public String getColumnName (Class clazz, String propertyName);
    
    
    /**
     * 根据类名，返回相应的表名称。如果传入的类为空，则抛出非法参数异常
     */
    public String getTableName (Class clazz);
    

    /**
     * 返回指定类需要持久化的属性名称（可能存在不可以持久化的名称）。
     * 如果传入的类为空，则抛出非法参数异常；如果类中不包含属性或者不
     * 包含可以持久化的属性，则抛出非法参数异常。
     * @param clazz Class 指定的类
     * @return String[] 属性名称数组
     */
    public String[] getPropertyNames (Class clazz);

//    /**
//     * 根据表名称及其对应的字段名称找到其对应的属性名称
//     * @param tableName
//     * @param columnName
//     * @return
//     */
//    public String getPropertyName(String tableName, String columnName);
    
    /**
     * 设置后续处理者。
     * @param dbMapping
     */
    public void setSuccessor (DBMapping dbMapping);
}
