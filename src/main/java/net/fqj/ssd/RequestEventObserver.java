/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 观察者接口.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public interface RequestEventObserver {

    /**
     * 每次请求完成后.
     *
     * @param request 请求参数.
     * @param response 返回数据.
     */
    void completed(HttpServletRequest request, HttpServletResponse response);
}
