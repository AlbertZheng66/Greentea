package com.xt.core.test.http;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: 模拟HttpServletRequest,以备测试之用.仅实现了getParameter相关的方法.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class MockHttpServletRequest
        implements HttpServletRequest
{
    private Hashtable parameters = new Hashtable();

    private Map<String, Object> attributes = new Hashtable<String, Object>();
    private String servletPath;

    public MockHttpServletRequest()
    {
    }

    public String getAuthType()
    {
        return null;
    }

    public String getContextPath()
    {
        return null;
    }

    public Cookie[] getCookies()
    {
        return null;
    }

    public long getDateHeader(String name)
    {
        return 0;
    }

    public String getHeader(String name)
    {
        return null;
    }

    public Enumeration getHeaderNames()
    {
        return null;
    }

    public Enumeration getHeaders(String name)
    {
        return null;
    }

    public int getIntHeader(String name)
    {
        return 0;
    }

    public String getMethod()
    {
        return null;
    }

    public String getPathInfo()
    {
        return null;
    }

    public String getPathTranslated()
    {
        return null;
    }

    public String getQueryString()
    {
        return null;
    }

    public String getRemoteUser()
    {
        return null;
    }

    public String getRequestedSessionId()
    {
        return null;
    }

    public String getRequestURI()
    {
        return null;
    }

    public StringBuffer getRequestURL()
    {
        return null;
    }

    public String getServletPath()
    {
        return servletPath;
    }
    
    public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public HttpSession getSession()
    {
        return null;
    }

    public HttpSession getSession(boolean create)
    {
        return null;
    }

    public java.security.Principal getUserPrincipal()
    {
        return null;
    }

    public boolean isRequestedSessionIdFromCookie()
    {
        return true;
    }

    public boolean isRequestedSessionIdFromUrl()
    {
        return true;
    }

    public boolean isRequestedSessionIdFromURL()
    {
        return true;
    }

    public boolean isRequestedSessionIdValid()
    {
        return true;
    }

    public boolean isUserInRole(String role)
    {
        return true;
    }

    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames()
    {
        return null;
    }

    public String getCharacterEncoding()
    {
        return null;
    }

    public int getContentLength()
    {
        return 0;
    }

    public String getContentType()
    {
        return null;
    }

    public ServletInputStream getInputStream()
    {
        return null;
    }

    public Locale getLocale()
    {
        return null;
    }

    public Enumeration getLocales()
    {
        return null;
    }

    public String getParameter(String name)
    {
        return (String) parameters.get(name);
    }

    public void addParameter(String name, String value)
    {
        parameters.put(name, value);
    }

    public Map getParameterMap()
    {
        return parameters;
    }

    public Enumeration getParameterNames()
    {
        return parameters.keys();
    }

    public String[] getParameterValues(String name)
    {
        String[] pvs = new String[parameters.size()];
        return (String[]) parameters.values().toArray(pvs);
    }

    public String getProtocol()
    {
        return null;
    }

    public java.io.BufferedReader getReader()
    {
        return null;
    }

    public String getRealPath(String path)
    {
        return null;
    }

    public String getRemoteHost()
    {
        return null;
    }

    public String getRemoteAddr()
    {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
        return null;
    }

    public String getScheme()
    {
        return null;
    }

    public String getServerName()
    {
        return null;
    }

    public int getServerPort()
    {
        return 0;
    }

    public boolean isSecure()
    {
        return true;
    }

    public void removeAttribute(String name)
    {
    	attributes.remove(name);
    }

    public void setAttribute(String name, Object obj)
    {
    	attributes.put(name, obj);
    }

    public void setCharacterEncoding(String env)
    {
    }

    public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void login(String string, String string1) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Part getPart(String string) throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getRemotePort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLocalPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext startAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
