package com.xt.core.val;


/**
 * 用于用户自定义的校验器接口.
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  校验字符串分为两部分，第一部分为校验器名称，一般以大写字段表示；
 * 第二部分为校验器需要的参数，以大括弧”{}“包围的部分；如：ID{0,6}，这个校验器
 * 表明他需要找到注册解析”ID“的校验器，"0,6"为其参数。
 * 注意：参数中不允许有”大括号“！</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-6
 */
public interface IValidator {
	
	/**
	 * Java正则表达式的适用类型
	 */
	public static final int JAVA_SCRIPT = 0;
	
	/**
	 * Java正则表达式的适用类型
	 */
	public static final int JAVA = 1;
	
	/**
	 * 设置标签的值
	 * @param value
	 */
	public void setValue (String value);
	
	/**
	 * 返回标识此校验器的标识
	 * @return
	 */
	public String getId ();
	
	/**
	 * 返回指定的正则表达式字符串. 传入时类型和名称均不能为空,
	 * 如果没有找到合适的表达式,则抛出异常.
	 * @param type 正则表达式适用的类型,在此接口中有其定义
	 * @return 
	 */
    public String getRegExpStr(int type) throws ValidatorException;
    
    /**
	 * 如果用户输入不符合要求,应该返回的错误信息.返回的信息中如果包含诸如”{title}"的变量。
	 * @param name 正则表达式的名称
	 * @return 错误信息. 
	 */
    public String getErrorMessage();
}
