/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import net.fqj.ssd.store.AbstractStore;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FqjSessionManager .
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class FqjSessionManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FqjSessionManager.class);

    /**
     * session store object.
     */
    private AbstractStore store;

    /**
     * session store object.
     *
     * @return the store
     */
    public AbstractStore getStore() {
        return store;
    }

    /**
     * session store object.
     *
     * @param store the store to set
     */
    public void setStore(final AbstractStore store) {
        this.store = store;
    }

    /**
     * 获取会话.
     *
     * @param request .
     * @param response .
     * @param requestEventSubject .
     * @param create .
     * @return .
     */
    public FqjHttpSession createSession(final SessionHttpServletRequestWrapper request,
            final HttpServletResponse response, final RequestEventSubject requestEventSubject,
            final boolean create) {

        final String sessionId = getRequestedSessionId(request);

        FqjHttpSession session = null;
        if (StringUtils.isEmpty(sessionId) && !create) {
            return null;
        }

        if (StringUtils.isNotEmpty(sessionId)) {
            final String persistenceId = generatorSessionKey(sessionId);
            //从持久化基础设施中查询会话数据
            session = this.store.load(persistenceId, store.getExpireTime());
            if (session != null) {
                session.setCookieSessionId(sessionId);
            }
        }
        if (session == null && create) {
            session = createEmptySession(request, response);
        }

        if (session != null) {
            attachEvent(session, request, response, requestEventSubject, store.getExpireTime());
            setCookie(session, request, response);
        }
        return session;
    }

    /**
     * 获取request 中的会话数据.
     *
     * @param request .
     * @return .
     */
    private String getRequestedSessionId(final HttpServletRequestWrapper request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        String sessionId = null;
        for (Cookie cookie : cookies) {
            if (!StringUtils.isBlank(store.getGlobalSessionCookieName())) {
                if (StringUtils.equals(store.getGlobalSessionCookieName(), cookie.getName())) {
                    sessionId = cookie.getValue();
                }
            } else if (StringUtils.equals(store.getSessionIdCookieName(), cookie.getName())) {
                sessionId = cookie.getValue();
            }
        }
        return sessionId;
    }

    /**
     * 创建一个新的session.
     *
     * @param request .
     * @param response .
     * @return .
     */
    private FqjHttpSession createEmptySession(final SessionHttpServletRequestWrapper request,
            final HttpServletResponse response) {
        final FqjHttpSession session = new FqjHttpSession();
        session.setCookieSessionId(createSessionId());
        session.setPersistenceId(generatorSessionKey(session.getCookieSessionId()));
        session.setCreationTime(System.currentTimeMillis());
        session.setIsNew(true);
        return session;
    }

    /**
     * 生成sessionId - 32位uuid.
     *
     * @return .
     */
    private String createSessionId() {
        return StringUtils.replace(UUID.randomUUID().toString()
                + UUID.randomUUID().toString() + UUID.randomUUID().toString(), "-", "");
    }

    /**
     * when request is completed,write session into redis and write cookie into
     * response.
     *
     *
     * @param session FqjHttpSession
     * @param request HttpServletRequestWrapper
     * @param response HttpServletResponse
     * @param requestEventSubject RequestEventSubject
     * @param sessionExpireTime expireTime
     */
    private void attachEvent(final FqjHttpSession session, final HttpServletRequestWrapper request,
            final HttpServletResponse response, final RequestEventSubject requestEventSubject,
            final int sessionExpireTime) {

        session.setListener(new SessionListenerAdaptor() {

            @Override
            public void onInvalidated(final FqjHttpSession session) {
                //删除会话数据
                session.setPersistenceId(generatorSessionKey(session.getId()));
                getStore().remove(session);
            }
        });

        requestEventSubject.attach(new RequestEventObserver() {
            @Override
            public void completed(final HttpServletRequest servletRequest, final HttpServletResponse response) {
                session.setLastAccessedTime(System.currentTimeMillis());
                if (session.isIsNew() || session.isIsDirty()) {
                    store.save(session, sessionExpireTime);
                }
            }
        });
    }

    /**
     * 设置cookie.
     *
     * @param session 会话数据.
     * @param request .
     * @param response .
     */
    private void setCookie(final FqjHttpSession session,
            final HttpServletRequestWrapper request, final HttpServletResponse response) {

        final Cookie cookie = new Cookie(store.getSessionIdCookieName(), null);
        cookie.setComment(store.getCookieComment());
        cookie.setDomain(store.getCookieDomain());
        cookie.setHttpOnly(store.isCookieHttpOnly());
        cookie.setPath(store.getCookiePath());
        cookie.setSecure(store.isCookieSecure());

        if (session.isExpired()) {
            cookie.setMaxAge(0);
        } else {
            switch (store.getCookieExpireType()) {
                case "time":
                    cookie.setMaxAge(store.getExpireTime());
                    break;
                default:
                    break;
            }
        }
        cookie.setValue(session.getId());
        response.addCookie(cookie);
    }

    /**
     * 生成redis中存入会话ID.
     *
     * @param sessionId cookie中的ID.
     * @return 。
     */
    private String generatorSessionKey(final String sessionId) {
        return store.getSessionIdPrefix().concat(DigestUtils.md5Hex(sessionId));
    }
}
