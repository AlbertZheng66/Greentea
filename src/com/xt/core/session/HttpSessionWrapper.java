package com.xt.core.session;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

public class HttpSessionWrapper implements Session, Serializable {
    private static final long serialVersionUID = -2583809906134992603L;

    public final HttpSession httpSession;

    public HttpSessionWrapper(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public Object getAttribute(String name) {
        return httpSession.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getAttributeNames() {
        return httpSession.getAttributeNames();
    }

    public long getCreationTime() {
        return httpSession.getCreationTime();
    }

    public String getId() {
        return httpSession.getId();
    }

    public long getLastAccessedTime() {
        return httpSession.getLastAccessedTime();
    }

    public int getMaxInactiveInterval() {
        return httpSession.getMaxInactiveInterval();
    }

    public void invalidate() {
        httpSession.getMaxInactiveInterval();
    }

    public void removeAttribute(String name) {
        httpSession.removeAttribute(name);
    }

    public void setAttribute(String name, Serializable value) {
        httpSession.setAttribute(name, value);
    }

    public void setLastAccessedTime(long accessedTime) {
        // nothing to do
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    /**
     * FIXME: 可以在某些情况下，是支持集群的。
     * @return
     */
    public boolean isClustered() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(super.toString()).append("[");
        strBld.append("httpSession=").append(httpSession);
        strBld.append("]");
        return strBld.toString();
    }

    public boolean isValid() {
        return (httpSession != null);
    }
}
