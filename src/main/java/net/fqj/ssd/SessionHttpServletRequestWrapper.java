/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * rewrite HttpServletRequest,change getSession method,use redis session
 * implement.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * response.
     */
    private final HttpServletResponse response;

    /**
     * httpsession.
     */
    private ShusiHttpSession httpSession;

    /**
     * manager.
     */
    private final ShusiSessionManager manager;

    /**
     * subject.
     */
    private final RequestEventSubject requestEventSubject;

    /**
     * 初始化HttpServletWrapper.
     *
     * @param request request
     * @param response response
     * @param manager session manager
     * @param requestEventSubject 观察者
     */
    public SessionHttpServletRequestWrapper(final HttpServletRequest request,
            final HttpServletResponse response, final ShusiSessionManager manager,
            final RequestEventSubject requestEventSubject) {
        super(request);
        this.response = response;
        this.manager = manager;
        this.requestEventSubject = requestEventSubject;
    }

    /**
     * get session.
     *
     * @param create 是否创建会话的标识 true : 没有会话时会创建 ， false 反之
     * @return HttpSession.
     */
    @Override
    public HttpSession getSession(final boolean create) {
        if (httpSession != null && !httpSession.isExpired()) {
            return httpSession;
        }
        httpSession = manager.createSession(this, response, requestEventSubject, create);
        return httpSession;
    }

    /**
     * get session.
     *
     * @return httpSession.
     */
    @Override
    public HttpSession getSession() {
        return getSession(true);
    }
}
