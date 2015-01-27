package com.xt.gt.jt.screen;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.exception.SystemException;

/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: 屏幕流处理器。但采用屏幕流时，返回的屏幕有赖中间结果，需要采用屏幕流处理器<br>
 *                 进行处理。processFlow()返回的字符串即是屏幕流的定义。
 *                 这个接口在ScreenFlowManager
 *                 中被调用。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface FlowHandler
    extends java.io.Serializable
{
    public String processFlow (HttpServletRequest request, Object ret)
        throws SystemException;
}
