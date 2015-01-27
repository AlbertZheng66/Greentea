package com.xt.core.session;

import java.io.Serializable;
import java.util.Enumeration;

public interface Session {

	public Object getAttribute(java.lang.String name);

	public Enumeration<String> getAttributeNames();

	/**
	 * Returns the time when this session was created, measured in milliseconds
	 * since midnight January 1, 1970 GMT.
	 * 
	 * @return
	 */
	public long getCreationTime();

	/**
	 * Returns a string containing the unique identifier assigned to this
	 * session.
	 * 
	 * @return
	 */
	public java.lang.String getId();

	/**
	 * Returns the last time the client sent a request associated with this
	 * session, as the number of milliseconds since midnight January 1, 1970
	 * GMT.
	 * 
	 * @return
	 */
	public long getLastAccessedTime();


        /**
         * 设置最后一次存取的时间
         * @param accessedTime
         */
        public void setLastAccessedTime(long accessedTime);

	/**
	 * Returns the maximum time interval, in seconds, that the servlet container
	 * will keep this session open between client accesses.
	 * 
	 * @return
	 */
       int getMaxInactiveInterval();

	/**
	 * Invalidates this session and unbinds any objects bound to it.
	 */
	public void invalidate();

	// boolean isNew()
	// Returns true if the client does not yet know about the session or if the
	// client chooses not to join the session.

	/**
	 * Removes the object bound with the specified name from this session.
	 * 
	 * @param name
	 */
	public void removeAttribute(java.lang.String name);

	/**
	 * Binds an object to this session, using the name specified.
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(java.lang.String name, Serializable value);

        /**
         * 是否支持集群
         * @return
         */
        public boolean isClustered();

	// void setMaxInactiveInterval(int interval)

        /**
         * 判断一个 Session 是否依然生效。
         * @return
         */
        public boolean isValid();
}
