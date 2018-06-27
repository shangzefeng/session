/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import net.fqj.ssd.FqjHttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * RedisStore.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class RedisStore extends AbstractStore {

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStore.class);
    
    /**
     * redis pool.
     */
    private final JedisPool jedisPool;

    /**
     * 构造类.
     *
     * @param jedisPool .
     */
    public RedisStore(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 会话数据保存.
     *
     * @param session
     */
    @Override
    @SuppressWarnings("null")
    public void save(final FqjHttpSession session, final int expireTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            final Map<String, String> map = new HashMap<>();
            map.put("creationTime", String.valueOf(session.getCreationTime()));
            map.put("lastAccessedTime", String.valueOf(session.getLastAccessedTime()));
            map.put("data", map2String(session.getData()));

            jedis.hmset(session.getPersistenceId(), map);
            jedis.expire(session.getPersistenceId(), expireTime);

        } catch (final Exception e) {
            LOGGER.error("del session exception : ", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 获取session数据.
     *
     * @param persistenceId
     * @return
     */
    @Override
    @SuppressWarnings("null")
    public FqjHttpSession load(final String persistenceId, final int expireTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            final Map<String, String> map = jedis.hgetAll(persistenceId);

            if (map.isEmpty()) {
                return null;
            }

            final FqjHttpSession session = new FqjHttpSession();

            session.setPersistenceId(persistenceId);
            session.setCreationTime(Long.parseLong(map.getOrDefault("creationTime", "0")));
            session.setLastAccessedTime(System.currentTimeMillis());
            session.setIsNew(false);
            session.setIsDirty(false);
            session.setData(this.string2Map(map.getOrDefault("data", null)));
            jedis.expire(persistenceId, expireTime);

            return session;
        } catch (final Exception e) {
            LOGGER.error("loadSession excetpin : ", e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 从redis删除session.
     *
     * @param session session data.
     */
    @SuppressWarnings("null")
    @Override
    public void remove(final FqjHttpSession session) {
        Jedis jedis = null;
        try {
            if (null == session) {
                return;
            }

            if (StringUtils.isBlank(session.getPersistenceId())) {
                return;
            }

            jedis = jedisPool.getResource();
            jedis.del(session.getPersistenceId());
        } catch (final Exception e) {
            LOGGER.error("del session exception : ", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * map convert string.
     *
     * @param map .
     * @return .
     */
    @SuppressWarnings("null")
    private String map2String(final Map<String, Object> map) {
        try {
            if (map.isEmpty()) {
                return "";
            }
            final JSONObject obj = JSONObject.fromObject(map);
            if (obj == null) {
                return "";
            }
            return obj.toString();
        } catch (final Exception e) {
            LOGGER.error("map convert string exception ", e);
            return "";
        }
    }

    /**
     * string convert map.
     *
     * @param str .
     * @return .
     */
    @SuppressWarnings("null")
    private Map string2Map(final String str) {
        try {
            final Map<String, Object> map = new HashMap<>();
            if (StringUtils.isBlank(str)) {
                return map;
            }

            final JSONObject obj = JSONObject.fromObject(str);
            if (null == obj) {
                return map;
            }

            final Set<String> keys = obj.keySet();
            for (final Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                final String key = iterator.next();
                map.put(key, obj.optString(key, ""));
            }
            return map;
        } catch (final Exception e) {
            LOGGER.error("string convert map error ", e);
            return new HashMap<>();
        }
    }
}
