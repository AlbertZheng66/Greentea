package com.xt.gt.jt.http;

import com.xt.proxy.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于封装“Servlet”相关参数（HttpServletRequest， HttpServletResponse）的上下文环境实现类。
 * @author albert
 */
public class ServletContext implements Context {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public ServletContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
