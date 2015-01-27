package com.xt.core.session;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LocalSession implements Session {

    private static LocalSession instance;
    private Map<String, Serializable> attributes;
    private String id;
    private int maxInactiveInterval = Integer.MAX_VALUE;
    private long lastAccessedTime;
    private final long creationTime;

    private LocalSession() {
        creationTime = System.currentTimeMillis();
        lastAccessedTime = System.currentTimeMillis();
        id = "id_" + creationTime;
    }

    synchronized static public LocalSession getInstance() {
        if (instance == null) {
            instance = new LocalSession();
        } else {
            instance.lastAccessedTime = System.currentTimeMillis();
        }
        return instance;
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
        attributes = null;
        id = null;
        instance = null;
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

    public void setLastAccessedTime(long accessedTime) {
        this.lastAccessedTime = accessedTime;
    }


    public boolean isClustered() {
        return false;
    }

    public boolean isValid() {
        return true;
    }
}
