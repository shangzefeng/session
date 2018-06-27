/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Event Observer Design pattern of Subject.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class RequestEventSubject {

    /**
     * 请求事件观察者实际处理对象.
     */
    private RequestEventObserver listener;

    /**
     * attach.
     *
     * @param eventObserver 请求事件观察者实际处理对象.
     */
    public void attach(final RequestEventObserver eventObserver) {
        listener = eventObserver;
    }

    /**
     * detach.
     */
    public void detach() {
        listener = null;
    }

    /**
     * deal.
     *
     * @param request request
     * @param response response
     */
    public void completed(final HttpServletRequest request, final HttpServletResponse response) {
        if (listener != null) {
            listener.completed(request, response);
        }
    }
}
