package com.xt.core.test.http;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * <p>Title: 框架类.</p>
 * <p>Description: HttpSession模拟类,用于模拟应用服务器的Session对象,而不需要启动Web服务器.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class MockHttpSession
    implements HttpSession
{
    public MockHttpSession()
    {
    }

    /**
     * getCreationTime
     *
     * @return long
     */
    public long getCreationTime ()
    {
        return 0L;
    }

    /**
     * getId
     *
     * @return String
     */
    public String getId ()
    {
        return "";
    }

    /**
     * getLastAccessedTime
     *
     * @return long
     */
    public long getLastAccessedTime ()
    {
        return 0L;
    }

    /**
     * getServletContext
     *
     * @return ServletContext
     */
    public ServletContext getServletContext ()
    {
        return null;
    }

    /**
     * setMaxInactiveInterval
     *
     * @param interval int
     */
    public void setMaxInactiveInterval (int interval)
    {
    }

    /**
     * getMaxInactiveInterval
     *
     * @return int
     */
    public int getMaxInactiveInterval ()
    {
        return 0;
    }

    /**
     * getSessionContext
     *
     * @return HttpSessionContext
     */
    public HttpSessionContext getSessionContext ()
    {
        return null;
    }

    /**
     * getAttribute
     *
     * @param name String
     * @return Object
     */
    public Object getAttribute (String name)
    {
        return "";
    }

    /**
     * getValue
     *
     * @param name String
     * @return Object
     */
    public Object getValue (String name)
    {
        return "";
    }

    /**
     * getAttributeNames
     *
     * @return Enumeration
     */
    public Enumeration getAttributeNames ()
    {
        return null;
    }

    /**
     * getValueNames
     *
     * @return String[]
     */
    public String[] getValueNames ()
    {
        return null;
    }

    /**
     * setAttribute
     *
     * @param name String
     * @param value Object
     */
    public void setAttribute (String name, Object value)
    {
    }

    /**
     * putValue
     *
     * @param name String
     * @param value Object
     */
    public void putValue (String name, Object value)
    {
    }

    /**
     * removeAttribute
     *
     * @param name String
     */
    public void removeAttribute (String name)
    {
    }

    /**
     * removeValue
     *
     * @param name String
     */
    public void removeValue (String name)
    {
    }

    /**
     * invalidate
     */
    public void invalidate ()
    {
    }

    /**
     * isNew
     *
     * @return boolean
     */
    public boolean isNew ()
    {
        return false;
    }

}
