package com.xt.gt.jt.screen;

/**
 * JSP文件基础路径。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 这个是框架留给可自定义查找JSP文件的钩子。如果系统对各个不同的页面有要求，
 * 即可能有项目级的要求，可以通过使用配置参数的方式，来切换到不同的显示页面上。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-30
 */
public interface IScreenFlowBasePath {
    public String getBasePath();
}
