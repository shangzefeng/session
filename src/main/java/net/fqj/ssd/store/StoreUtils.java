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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * util.
 *
 * @author FengShangZe
 * @version 1.0.0.1, Jun 22, 2017
 */
public final class StoreUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreUtils.class);

    /**
     * .
     */
    private StoreUtils() {
    }

    /**
     * map convert string.
     *
     * @param map .
     * @return .
     */
    @SuppressWarnings("null")
    public static String map2String(final Map<String, Object> map) {
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
    public static Map string2Map(final String str) {
        try {
            final Map<String, Object> map = new HashMap<>();
            if (StringUtils.isBlank(str)) {
                return map;
            }

            final JSONObject obj = JSONObject.fromObject(str);
            if (null == obj) {
                return map;
            }

            JSONObject.toBean(obj, HashMap.class, map);

            return map;
        } catch (final Exception e) {
            LOGGER.error("string convert map error ", e);
            return new HashMap<>();
        }
    }

    /**
     * post.
     *
     * @param uri 请求url.
     * @param params 参数可选.
     * @return 运行结果.
     */
    @SuppressWarnings("UnusedAssignment")
    public static JSONObject doPost(final String uri, final JSONObject params) {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        final JSONObject result = new JSONObject();
        result.put("succeed", true);

        try {
            httpClient = HttpClients.createDefault();

            final HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            final List<NameValuePair> qparams = new ArrayList<>();
            if (!params.isEmpty()) {
                final Set<String> keys = params.keySet();
                for (final Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                    final String key = iterator.next();
                    final String value = params.getOrDefault(key, "").toString();
                    qparams.add(new BasicNameValuePair(key, value));
                }
            }

            final UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(qparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            response = httpClient.execute(httpPost);
            if (null == response) {
                result.put("succeed", false);
                return result;
            }
            final String str = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

            final JSONObject obj = JSONObject.fromObject(str);
            if (null == obj) {
                result.put("succeed", false);
                return result;
            }

            if (StringUtils.equals(obj.optString("succeed", "false"), "false")) {
                result.put("succeed", false);
                return result;
            }

            result.put("data", obj.optJSONObject("data"));

        } catch (final IOException | UnsupportedOperationException e) {
            result.put("succeed", false);
            LOGGER.error("web client send request exception ", e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (final Exception e) {
                result.put("succeed", false);
                LOGGER.error("close web request client exception ", e);
            }
        }
        return result;
    }
}
