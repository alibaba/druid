/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.StringUtils;

public class WebAppStatManager {

    public final static String             SYS_PROP_INSTANCES = "druid.web.webAppStat";

    private final static WebAppStatManager instance           = new WebAppStatManager();

    private Set<Object>                    webAppStatSet      = null;

    public static WebAppStatManager getInstance() {
        return instance;
    }
    
    public synchronized WebAppStat getWebAppStat(String contextPath) {
        Set<Object> stats = getWebAppStatSet();
        for (Object item : stats) {
            if (item instanceof WebAppStat) {
                WebAppStat stat = (WebAppStat) item;
                if (StringUtils.equals(stat.getContextPath(), contextPath)) {
                    return stat;
                }
            }
        }
        
        WebAppStat stat = new WebAppStat(contextPath);
        this.addWebAppStatSet(stat);
        return stat;
    }

    public Set<Object> getWebAppStatSet() {
        if (webAppStatSet == null) {
            if (DruidDataSourceStatManager.isRegisterToSystemProperty()) {
                webAppStatSet = getWebAppStatSet0();
            } else {
                webAppStatSet = new CopyOnWriteArraySet<Object>();                
            }
        }

        return webAppStatSet;
    }

    public List<Map<String, Object>> getWebAppStatData() {
        Set<Object> stats = getWebAppStatSet();

        List<Map<String, Object>> statDataList = new ArrayList<Map<String, Object>>(stats.size());

        for (Object stat : stats) {
            Map<String, Object> statData = WebAppStatUtils.getStatData(stat);
            statDataList.add(statData);
        }

        return statDataList;
    }

    public List<Map<String, Object>> getURIStatData() {
        Set<Object> stats = getWebAppStatSet();

        List<Map<String, Object>> allAppUriStatDataList = new ArrayList<Map<String, Object>>();

        for (Object stat : stats) {
            List<Map<String, Object>> uriStatDataList = WebAppStatUtils.getURIStatDataList(stat);
            allAppUriStatDataList.addAll(uriStatDataList);
        }

        return allAppUriStatDataList;
    }

    public List<Map<String, Object>> getSessionStatData() {
        Set<Object> stats = getWebAppStatSet();

        List<Map<String, Object>> allAppUriStatDataList = new ArrayList<Map<String, Object>>();

        for (Object stat : stats) {
            List<Map<String, Object>> uriStatDataList = WebAppStatUtils.getSessionStatDataList(stat);
            allAppUriStatDataList.addAll(uriStatDataList);
        }

        return allAppUriStatDataList;
    }
    
    public Map<String, Object> getSessionStat(String sessionId) {
        Set<Object> stats = getWebAppStatSet();

        for (Object stat : stats) {
            Map<String, Object> statData = WebAppStatUtils.getSessionStatData(stat, sessionId);
            if (statData != null) {
                return statData;
            }
        }
        
        return null;
    }
    
    public Map<String, Object> getURIStatData(String uri) {
        Set<Object> stats = getWebAppStatSet();
        
        for (Object stat : stats) {
            Map<String, Object> statData = WebAppStatUtils.getURIStatData(stat, uri);
            if (statData != null) {
                return statData;
            }
        }
        
        return null;
    }

    public void addWebAppStatSet(Object webAppStat) {
        getWebAppStatSet().add(webAppStat);
    }

    public boolean remove(Object webAppStat) {
        return getWebAppStatSet().remove(webAppStat);
    }

    @SuppressWarnings("unchecked")
    static Set<Object> getWebAppStatSet0() {
        Properties properties = System.getProperties();
        Set<Object> webAppStats = (Set<Object>) properties.get(SYS_PROP_INSTANCES);

        if (webAppStats == null) {
            synchronized (properties) {
                webAppStats = (Set<Object>) properties.get(SYS_PROP_INSTANCES);

                if (webAppStats == null) {
                    webAppStats = new CopyOnWriteArraySet<Object>();
                    properties.put(SYS_PROP_INSTANCES, webAppStats);
                }
            }
        }

        return webAppStats;
    }

    public void resetStat() {
        Set<Object> stats = getWebAppStatSet();

        for (Object stat : stats) {
            WebAppStatUtils.reset(stat);
        }
    }

}
