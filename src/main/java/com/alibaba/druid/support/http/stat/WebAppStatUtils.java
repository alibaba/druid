/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.support.http.stat;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class WebAppStatUtils {

    private final static Log LOG = LogFactory.getLog(WebAppStatUtils.class);

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getStatData(Object webStat) {
        if (webStat.getClass() == WebAppStat.class) {
            return ((WebAppStat) webStat).getStatData();
        }

        try {
            Method method = webStat.getClass().getMethod("getStatData");
            Object obj = method.invoke(webStat);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getStatData error", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getURIStatDataList(Object webStat) {
        if (webStat.getClass() == WebAppStat.class) {
            return ((WebAppStat) webStat).getURIStatDataList();
        }

        try {
            Method method = webStat.getClass().getMethod("getURIStatDataList");
            Object obj = method.invoke(webStat);
            return (List<Map<String, Object>>) obj;
        } catch (Exception e) {
            LOG.error("getURIStatDataList error", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getSessionStatDataList(Object webStat) {
        if (webStat.getClass() == WebAppStat.class) {
            return ((WebAppStat) webStat).getSessionStatDataList();
        }

        try {
            Method method = webStat.getClass().getMethod("getSessionStatDataList");
            Object obj = method.invoke(webStat);
            return (List<Map<String, Object>>) obj;
        } catch (Exception e) {
            LOG.error("getSessionStatDataList error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSessionStatData(Object webStat, String sessionId) {
        if (webStat.getClass() == WebAppStat.class) {
            return ((WebAppStat) webStat).getSessionStatData(sessionId);
        }
        
        try {
            Method method = webStat.getClass().getMethod("getSessionStatData", String.class);
            Object obj = method.invoke(webStat, sessionId);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getSessionStatData error", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getURIStatData(Object webStat, String uri) {
        if (webStat.getClass() == WebAppStat.class) {
            return ((WebAppStat) webStat).getURIStatData(uri);
        }
        
        try {
            Method method = webStat.getClass().getMethod("getURIStatData", String.class);
            Object obj = method.invoke(webStat, uri);
            return (Map<String, Object>) obj;
        } catch (Exception e) {
            LOG.error("getURIStatData error", e);
            return null;
        }
    }

    public static void reset(Object webStat) {
        if (webStat.getClass() == WebAppStat.class) {
            ((WebAppStat) webStat).reset();
            return;
        }

        try {
            Method method = webStat.getClass().getMethod("reset");
            method.invoke(webStat);
        } catch (Exception e) {
            LOG.error("reset error", e);
        }
    }
}
