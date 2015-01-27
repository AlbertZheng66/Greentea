package com.xt.core.session;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BaseSession implements Session {

    private Map<String, Serializable> attributes;
    private String id;
    // 最大的时间间隔，默认为30分钟。
    private int maxInactiveInterval = 30 * 60 * 1000; // Integer.MAX_VALUE;
    private long lastAccessedTime;
    private final long creationTime;
    private boolean validate = true;

    public BaseSession() {
        creationTime = System.currentTimeMillis();
        lastAccessedTime = System.currentTimeMillis();
    }

    public Object getAttribute(String name) {
        if (attributes == null || name == null) {
            return null;
        }
        return attributes.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        if (attributes == null) {
            return null;
        }
        Vector<String> names = new Vector<String>(attributes.size());
        names.addAll(attributes.keySet());
        return names.elements();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void invalidate() {
        id = null;
        validate = false;
        attributes = null;
    }

    public void removeAttribute(String name) {
        if (attributes != null && name != null && attributes.containsKey(name)) {
            attributes.remove(name);
        }
    }

    public void setAttribute(String name, Serializable value) {
        if (attributes == null) {
            attributes = new HashMap<String, Serializable>();
        }
        if (name != null && value != null) {
            attributes.put(name, value);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public boolean isClustered() {
        return false;
    }

    public boolean isValid() {
        return true;
    }
}
