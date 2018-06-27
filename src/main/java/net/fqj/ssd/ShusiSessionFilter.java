/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rewrite HttpServletRequest,change getSession method,use redis session .
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class ShusiSessionFilter implements Filter {

    /**
     * 过滤.
     */
    public static final String[] IGNORE_SUFFIX = new String[]{".png", ".jpg", ".jpeg", ".gif", ".css", ".js", ".html", ".htm"};

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ShusiSessionFilter.class);

    /**
     * 会话管理对象.
     */
    private ShusiSessionManager manager;

    /**
     * init.
     *
     * @param filterConfig .
     * @throws ServletException .
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * set manager.
     *
     * @param manager .
     */
    public void setSessionManager(final ShusiSessionManager manager) {
        this.manager = manager;
    }

    /**
     * filter.
     *
     * @param servletRequest .
     * @param servletResponse .
     * @param filterChain .
     * @throws IOException .
     * @throws ServletException .
     */
    @Override
    public void doFilter(final ServletRequest servletRequest,
            final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!shouldFilter(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        final RequestEventSubject eventSubject = new RequestEventSubject();
        
        final SessionHttpServletRequestWrapper requestWrapper
                = new SessionHttpServletRequestWrapper(request, response, manager, eventSubject);
        
        try {
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (final IOException | ServletException e) {
            LOGGER.error("doFilter exception ", e);
        } finally {
            //when request is completed,refresh session event,write cookie or save into redis
            eventSubject.completed(request, response);
        }
    }

    /**
     * igonre image,css or javascript file request.
     *
     * @param request HttpServletRequest .
     * @return .
     */
    private boolean shouldFilter(final HttpServletRequest request) {
        final String uri = request.getRequestURI().toLowerCase();
        for (String suffix : IGNORE_SUFFIX) {
            if (uri.endsWith(suffix)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void destroy() {
    }
}
