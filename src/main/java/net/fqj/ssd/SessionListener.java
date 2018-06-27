/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd;

/**
 * http session listener.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public interface SessionListener {

    /**
     * 设置会话属性变化时触发.
     *
     * @param session HttpSession.
     */
    void onAttributeChanged(ShusiHttpSession session);

    /**
     * 会话失效时触发.
     *
     * @param session HttpSession.
     */
    void onInvalidated(ShusiHttpSession session);
}
