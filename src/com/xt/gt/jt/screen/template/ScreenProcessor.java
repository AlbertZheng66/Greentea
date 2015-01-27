package com.xt.gt.jt.screen.template;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface ScreenProcessor
{
    /**
     * 返回下一个屏幕定义的名称。
     * @param request Http请求
     * @return 屏幕定义的名称
     */
    public String getNextScreen (HttpServletRequest request);

    /**
     * 返回上一个屏幕定义的名称（当用户未通过校验，事务逻辑发生错误时，返回到上一页）。
     * @param request Http请求
     * @return 屏幕定义的名称
     */
    public String getPreviousScreen(HttpServletRequest request);
}
