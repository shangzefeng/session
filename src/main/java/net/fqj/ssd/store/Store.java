/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fqj.ssd.store;

import net.fqj.ssd.ShusiHttpSession;

/**
 * 会话存储接口.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public interface Store {

    /**
     * Load and return the Session associated with the specified session
 identifier from this Store, without removing it. If there is no such
     * stored Session, return <code>null</code>.
     *
     * @param id Session identifier of the session to load
     * @param expireTime Session expire time
     *
     * @return the loaded Session instance
     */
    ShusiHttpSession load(String id, int expireTime);

    /**
     * Remove the Session with the specified session identifier from this Store,
 if present. If no such Session is present, this method takes no action.
     *
     * @param session identifier of the Session to be removed
     *
     */
    void remove(ShusiHttpSession session);

    /**
     * Save the specified Session into this Store. Any previously saved
     * information for the associated session identifier is replaced.
     *
     * @param session Session to be saved
     * @param expireTime Session expire time
     *
     */
    void save(ShusiHttpSession session, int expireTime);
}
