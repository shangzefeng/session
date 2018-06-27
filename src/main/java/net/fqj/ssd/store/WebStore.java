/*
 * Copyright (c) 2017, shusi.net
 *
 * All rights reserved.
 */
package net.fqj.ssd.store;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import net.fqj.ssd.FqjHttpSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RedisStore.
 *
 * @author fsz
 * @version 1.0.0.0, Oct 12, 2017
 * @since 1.0.0
 */
public class WebStore extends AbstractStore {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebStore.class);

    /**
     * appKey.
     */
    private String appKey;

    /**
     * url root.
     */
    private String urlRoot;

    /**
     * save rule : UNLIMITED:无限制 SINGLE_ONLINE:一个账号某时刻只能在全系统中使用
     * SUBSYSTEM_SINGLE_ONLINE:一个账号只能在全系统中的某个子系统中使用.
     */
    private String saveRule = "UNLIMITED";

    /**
     * 构造类.
     */
    public WebStore() {
    }

    /**
     * 会话数据保存.
     *
     * @param session
     */
    @Override
    @SuppressWarnings("null")
    public void save(final FqjHttpSession session, final int expireTime) {
        try {

            final String serviceUrl = "/web-session/save";

            final JSONObject header = new JSONObject();
            header.put("appKey", getAppKey());

            final JSONObject data = new JSONObject();
            data.put("persistenceId", session.getPersistenceId());
            data.put("expireTime", expireTime);
            data.put("creationTime", session.getCreationTime());
            data.put("lastAccessedTime", session.getLastAccessedTime());
            data.put("saveRule", getSaveRule());

            final JSONObject attr = new JSONObject();

            final Map<String, Object> map = session.getData();
            if (!map.isEmpty()) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    final String key = entry.getKey();
                    final Object value = entry.getValue() == null ? "" : entry.getValue();
                    attr.put(key, value.toString());
                }
            }
            data.put("attr", attr);

            final JSONObject param = new JSONObject();
            param.put("header", header);
            param.put("data", data);

            StoreUtils.doPost(getUrlRoot() + serviceUrl, param);

        } catch (final Exception e) {
            LOGGER.error("save session exception : ", e);
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
        try {

            final String serviceUrl = "/web-session/load";

            final JSONObject header = new JSONObject();
            header.put("appKey", getAppKey());

            final JSONObject data = new JSONObject();
            data.put("persistenceId", persistenceId);
            data.put("expireTime", expireTime);

            final JSONObject param = new JSONObject();
            param.put("header", header);
            param.put("data", data);

            final JSONObject result = StoreUtils.doPost(getUrlRoot() + serviceUrl, param);
            if (result.isEmpty() || StringUtils.equals("false", result.optString("succeed", "false"))) {
                return null;
            }

            if (null == result.optJSONObject("data")) {
                return null;
            }

            final JSONObject response = result.optJSONObject("data");

            //构造会话信息
            final FqjHttpSession session = new FqjHttpSession();

            session.setPersistenceId(persistenceId);
            session.setLastAccessedTime(System.currentTimeMillis());
            session.setIsNew(false);
            session.setIsDirty(false);
            session.setDriverInfo(response.optString("driverInfo", ""));
            session.setCreationTime(response.optLong("creationTime", 0L));

            final Map<String, Object> map = session.getData();

            final String attrStr = response.optString("attr", null);
            if (!StringUtils.isBlank(attrStr)) {
                final JSONObject attr = JSONObject.fromObject(attrStr);
                if (!attr.isEmpty()) {
                    final Set<String> keys = attr.keySet();
                    for (final Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                        final String key = iterator.next();
                        map.put(key, attr.optString(key, ""));
                    }
                }
            }

            return session;
        } catch (final Exception e) {
            LOGGER.error("load session excetpin : ", e);
            return null;
        }
    }

    /**
     * 从redis删除session.
     *
     * @param session
     */
    @SuppressWarnings("null")
    @Override
    public void remove(final FqjHttpSession session) {
        try {
            final String serviceUrl = "/web-session/remove";

            if (null == session) {
                return;
            }

            if (StringUtils.isBlank(session.getPersistenceId())) {
                return;
            }

            session.setLastAccessedTime(System.currentTimeMillis());
            session.setIsNew(false);
            session.setIsDirty(false);
            session.setExpired(false);

            final JSONObject header = new JSONObject();
            header.put("appKey", getAppKey());

            final JSONObject data = new JSONObject();
            data.put("persistenceId", session.getPersistenceId());

            final JSONObject param = new JSONObject();
            param.put("header", header);
            param.put("data", data);

            StoreUtils.doPost(getUrlRoot() + serviceUrl, param);

        } catch (final Exception e) {
            LOGGER.error("del session exception : ", e);
        }
    }

    /**
     * appKey.
     *
     * @return the appKey
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * appKey.
     *
     * @param appKey the appKey to set
     */
    public void setAppKey(final String appKey) {
        this.appKey = appKey;
    }

    /**
     * url root.
     *
     * @return the urlRoot
     */
    public String getUrlRoot() {
        return urlRoot;
    }

    /**
     * url root.
     *
     * @param urlRoot the urlRoot to set
     */
    public void setUrlRoot(final String urlRoot) {
        this.urlRoot = urlRoot;
    }

    /**
     * save rule : UNLIMITED:无限制 SINGLE_ONLINE:一个账号某时刻只能在全系统中使用
     * SUBSYSTEM_SINGLE_ONLINE:一个账号只能在全系统中的某个子系统中使用.
     *
     * @return the saveRule
     */
    public String getSaveRule() {
        return saveRule;
    }

    /**
     * save rule : UNLIMITED:无限制 SINGLE_ONLINE:一个账号某时刻只能在全系统中使用
     * SUBSYSTEM_SINGLE_ONLINE:一个账号只能在全系统中的某个子系统中使用.
     *
     * @param saveRule the saveRule to set
     */
    public void setSaveRule(final String saveRule) {
        this.saveRule = saveRule;
    }
}
