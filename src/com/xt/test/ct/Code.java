package com.xt.test.ct;


/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 用于代码表的查询。这个代码表是指数据库中的代码表，一般情况下，用于向用户
 *                 显示信息时的替换。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface Code
{
    /**
     * 根据编码查找相应的编码属性
     * @param code 编码
     * @return 对应于编码的实例
     */
    public CodeValue find (String code);
}
