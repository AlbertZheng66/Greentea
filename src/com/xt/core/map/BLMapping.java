package com.xt.core.map;

/**
 * 事务逻辑映射。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  将一个HTTP请求映射为相应的映射业务处理类即处理方法。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-7-9
 */
 
public interface BLMapping {
	
	/**
	 * 根据HTTP请求得到映射的方法。如果参数为空，将抛出BadParameterException异常。
	 * 如果参数中存在一个或者多个(多个连续视为一个)“\”，将其转换为"/"。
	 * @param url 去掉ContextPath后的URL请求
	 * @return
	 */
    public String mapHandler (String url);
    
    public String mapMethod (String url);
}
