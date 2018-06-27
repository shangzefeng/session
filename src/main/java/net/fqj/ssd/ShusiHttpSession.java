/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpSession Object in redis.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class ShusiHttpSession implements HttpSession, Serializable {

    /**
     * .
     */
    private static final long serialVersionUID = 1L;

    /**
     * creationTime.
     */
    private long creationTime = 0L;

    /**
     * cookie对应的sessionId.
     */
    private String cookieSessionId;

    /**
     * 持久化存储的sessionId.
     */
    private String persistenceId;

    /**
     * maxInactiveInterval.
     */
    private int maxInactiveInterval;

    /**
     * lastAccessedTime.
     */
    private long lastAccessedTime = 0;

    /**
     * 设备信息.
     */
    private String driverInfo;

    /**
     * 会话是否已过期.
     */
    private transient boolean expired = false;

    /**
     * 是否为新建会话.
     */
    private transient boolean isNew = false;

    /**
     * 会话数据是否是脏数据，需要同步到持久化中.
     */
    private transient boolean isDirty = false;

    /**
     * listener.
     */
    private transient SessionListener listener;

    /**
     * session 属性数据.
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 设置listener.
     *
     * @param listener listener.
     */
    public void setListener(final SessionListener listener) {
        this.listener = listener;
    }

    /**
     * 获取创建会话的时间.
     *
     * @return 创建时间.
     */
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 获取会话ID.
     *
     * @return .
     */
    @Override
    public String getId() {
        return getCookieSessionId();
    }

    /**
     * 获取最后访问时间.
     *
     * @return .
     */
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /**
     * 设置最后访问时间.
     *
     * @param lastAccessedTime .
     */
    public void setLastAccessedTime(final long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    /**
     * get context.
     *
     * @return .
     */
    @Override
    public ServletContext getServletContext() {
        return null;
    }

    /**
     * 设置活动时间间隔.
     *
     * @param intervalTime.
     */
    @Override
    public void setMaxInactiveInterval(final int intervalTime) {
        this.maxInactiveInterval = intervalTime;
    }

    /**
     * 获取活动时间间隔.
     *
     * @return
     */
    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * 获取属性值.
     *
     * @param key 属性key.
     * @return
     */
    @Override
    public Object getAttribute(final String key) {
        return getData().get(key);
    }

    /**
     * 获取属性值.
     *
     * @param key 属性key.
     * @return
     */
    @Override
    public Object getValue(final String key) {
        return getData().get(key);
    }

    @Override
    public Enumeration getAttributeNames() {
        final Iterator iterator = getData().keySet().iterator();
        return new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * 获取属性keys.
     *
     * @return
     */
    @Override
    public String[] getValueNames() {
        final String[] names = new String[getData().size()];
        return getData().keySet().toArray(names);
    }

    /**
     * 设置属性数据.
     *
     * @param key 属性key.
     * @param value 属性值.
     */
    @Override
    public void setAttribute(final String key, final Object value) {
        getData().put(key, value);
        setIsDirty(true);
    }

    /**
     * 设置属性数据.
     *
     * @param key 属性key.
     * @param value 属性值.
     */
    @Override
    public void putValue(final String key, final Object value) {
        setAttribute(key, value);
    }

    /**
     * 删除属性数据.
     *
     * @param key 要删除的属性key.
     */
    @Override
    public void removeAttribute(final String key) {
        getData().remove(key);
        setIsDirty(true);
    }

    /**
     * 删除属性数据.
     *
     * @param key 要删除的属性key.
     */
    @Override
    public void removeValue(final String key) {
        this.removeAttribute(key);
    }

    /**
     * session失效.
     */
    @Override
    public void invalidate() {
        setExpired(true);
        setIsDirty(true);
        if (listener != null) {
            listener.onInvalidated(this);
        }
    }

    /**
     * 判断是否为新创建的会话.
     *
     * @return
     */
    @Override
    public boolean isNew() {
        return isIsNew();
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(final Map<String, Object> data) {
        this.data = data;
    }

    /**
     * creationTime.
     *
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(final long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * 持久化存储的sessionId.
     *
     * @return the persistenceId
     */
    public String getPersistenceId() {
        return persistenceId;
    }

    /**
     * 持久化存储的sessionId.
     *
     * @param persistenceId the persistenceId to set
     */
    public void setPersistenceId(final String persistenceId) {
        this.persistenceId = persistenceId;
    }

    /**
     * cookie对应的sessionId.
     *
     * @return the cookieSessionId
     */
    public String getCookieSessionId() {
        return cookieSessionId;
    }

    /**
     * cookie对应的sessionId.
     *
     * @param cookieSessionId the cookieSessionId to set
     */
    public void setCookieSessionId(final String cookieSessionId) {
        this.cookieSessionId = cookieSessionId;
    }

    /**
     * 会话是否已过期.
     *
     * @return the expired
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * 会话是否已过期.
     *
     * @param expired the expired to set
     */
    public void setExpired(final boolean expired) {
        this.expired = expired;
    }

    /**
     * 是否为新建会话.
     *
     * @return the isNew
     */
    public boolean isIsNew() {
        return isNew;
    }

    /**
     * 是否为新建会话.
     *
     * @param isNew the isNew to set
     */
    public void setIsNew(final boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * 会话数据是否是脏数据，需要同步到持久化中.
     *
     * @return the isDirty
     */
    public boolean isIsDirty() {
        return isDirty;
    }

    /**
     * 会话数据是否是脏数据，需要同步到持久化中.
     *
     * @param isDirty the isDirty to set
     */
    public void setIsDirty(final boolean isDirty) {
        this.isDirty = isDirty;
    }

    /**
     * 设备信息.
     * @return the driverInfo
     */
    public String getDriverInfo() {
        return driverInfo;
    }

    /**
     * 设备信息.
     * @param driverInfo the driverInfo to set
     */
    public void setDriverInfo(final String driverInfo) {
        this.driverInfo = driverInfo;
    }
}
