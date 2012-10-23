/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.spring.stat.SpringStatManager;

public class PushService {

    private final DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();

    private String                       serverUrl;

    private boolean                      includeSql        = true;

    private boolean                      includeWebApp     = true;
    private boolean                      includeWebUri     = true;
    private boolean                      includeWebSession = true;
    private boolean                      includeSpring     = true;

    public void collect() {
        Map<String, Object> data = new HashMap<String, Object>();

        if (includeSql) {
            List<Map<String, Object>> dataSources = statManagerFacade.getDataSourceStatDataList(true);

            data.put("DataSources", dataSources);
        }

        if (includeWebApp) {
            List<Map<String, Object>> list = WebAppStatManager.getInstance().getWebAppStatData();
            data.put("WebApp", list);
        }

        if (includeWebUri) {
            List<Map<String, Object>> list = WebAppStatManager.getInstance().getURIStatData();

            data.put("WebURI", list);
        }

        if (includeWebSession) {
            List<Map<String, Object>> list = WebAppStatManager.getInstance().getSessionStatData();

            data.put("WebSession", list);
        }

        if (includeSpring) {
            List<Map<String, Object>> list = SpringStatManager.getInstance().getMethodStatData();

            data.put("Spring", list);
        }
    }

    protected void push(Map<String, Object> data) {
        String json = JSONUtils.toJSONString(data);
        //TODO URL Connection Post
    }
}
