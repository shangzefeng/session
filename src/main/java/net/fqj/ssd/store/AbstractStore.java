/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd.store;

/**
 * AbstractStore.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public abstract class AbstractStore implements Store {

    /**
     * 默认会话超时时间.
     */
    private static final int DEFAULT_SESSION_EXPIRE_TIME = 1800;

    /**
     * redis中存入会话的前缀.
     */
    private String sessionIdPrefix = "SHUSI-SESSIONID:";

    /**
     * session cookie name.
     */
    private String sessionIdCookieName = "SHUSI-JSESSIONID";

    /**
     * global session cookie name.
     */
    private String globalSessionCookieName = "";

    /**
     * 会话过期时间.
     */
    private int expireTime = DEFAULT_SESSION_EXPIRE_TIME;

    /**
     * cookie path.
     */
    private String cookiePath = "/";

    /**
     * cookie http only.
     */
    private boolean cookieHttpOnly = true;

    /**
     * http comment.
     */
    private String cookieComment = "";

    /**
     * cookie domain.
     */
    private String cookieDomain = "";

    /**
     * cookie secure.
     */
    private boolean cookieSecure = false;

    /**
     * cookie 过期类别 session或过期时间.
     */
    private String cookieExpireType = "session";

    /**
     * redis中存入会话的前缀.
     *
     * @return the sessionIdPrefix
     */
    public String getSessionIdPrefix() {
        return sessionIdPrefix;
    }

    /**
     * redis中存入会话的前缀.
     *
     * @param sessionIdPrefix the sessionIdPrefix to set
     */
    public void setSessionIdPrefix(final String sessionIdPrefix) {
        this.sessionIdPrefix = sessionIdPrefix;
    }

    /**
     * session cookie name.
     *
     * @return the sessionIdCookieName
     */
    public String getSessionIdCookieName() {
        return sessionIdCookieName;
    }

    /**
     * session cookie name.
     *
     * @param sessionIdCookieName the sessionIdCookieName to set
     */
    public void setSessionIdCookieName(final String sessionIdCookieName) {
        this.sessionIdCookieName = sessionIdCookieName;
    }

    /**
     * 会话过期时间.
     *
     * @return the expireTime
     */
    public int getExpireTime() {
        return expireTime;
    }

    /**
     * 会话过期时间.
     *
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(final int expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * cookie path.
     *
     * @return the cookiePath
     */
    public String getCookiePath() {
        return cookiePath;
    }

    /**
     * cookie path.
     *
     * @param cookiePath the cookiePath to set
     */
    public void setCookiePath(final String cookiePath) {
        this.cookiePath = cookiePath;
    }

    /**
     * cookie http only.
     *
     * @return the cookieHttpOnly
     */
    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }

    /**
     * cookie http only.
     *
     * @param cookieHttpOnly the cookieHttpOnly to set
     */
    public void setCookieHttpOnly(final boolean cookieHttpOnly) {
        this.cookieHttpOnly = cookieHttpOnly;
    }

    /**
     * http comment.
     *
     * @return the cookieComment
     */
    public String getCookieComment() {
        return cookieComment;
    }

    /**
     * http comment.
     *
     * @param cookieComment the cookieComment to set
     */
    public void setCookieComment(final String cookieComment) {
        this.cookieComment = cookieComment;
    }

    /**
     * cookie domain.
     *
     * @return the cookieDomain
     */
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * cookie domain.
     *
     * @param cookieDomain the cookieDomain to set
     */
    public void setCookieDomain(final String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * cookie secure.
     *
     * @return the cookieSecure
     */
    public boolean isCookieSecure() {
        return cookieSecure;
    }

    /**
     * cookie secure.
     *
     * @param cookieSecure the cookieSecure to set
     */
    public void setCookieSecure(final boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    /**
     * cookie 过期类别.
     *
     * @return the cookieExpireType
     */
    public String getCookieExpireType() {
        return cookieExpireType;
    }

    /**
     * cookie 过期类别.
     *
     * @param cookieExpireType the cookieExpireType to set
     */
    public void setCookieExpireType(final String cookieExpireType) {
        this.cookieExpireType = cookieExpireType;
    }

    /**
     * global session cookie name.
     * @return the globalSessionCookieName
     */
    public String getGlobalSessionCookieName() {
        return globalSessionCookieName;
    }

    /**
     * global session cookie name.
     * @param globalSessionCookieName the globalSessionCookieName to set
     */
    public void setGlobalSessionCookieName(final String globalSessionCookieName) {
        this.globalSessionCookieName = globalSessionCookieName;
    }

}
