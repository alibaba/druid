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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.support.http.stat.WebAppStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.spring.stat.SpringStatManager;
import com.alibaba.druid.util.DaemonThreadFactory;
import com.alibaba.druid.util.HttpClientUtils;

/**
 * <pre>
 * 将指定的信息 (includeSql,includeWebApp,includeWebUri,includeWebSession,includeSpring)
 * 定期 (cycleTime)
 * post到指定的url (serverUrl) ，请求超时时间（pushTimeOut）
 * 并进行清零 (autoReset)
 * </pre>
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class PushService {

    private final static PushService     instance             = new PushService();
    private static final int             DEFAULT_CYCLE_TIME   = 5 * 60 * 1000;
    private static final int             DEFAULT_PUSH_TIMEOUT = 60 * 1000;

    private final DruidStatManagerFacade statManagerFacade    = DruidStatManagerFacade.getInstance();
    private final WebAppStatManager      webAppStatManager    = WebAppStatManager.getInstance();
    private final SpringStatManager      springStatManager    = SpringStatManager.getInstance();

    private String                       serverUrl;

    private boolean                      includeSql           = true;
    private boolean                      includeWebApp        = true;
    private boolean                      includeWebUri        = true;
    private boolean                      includeWebSession    = true;
    private boolean                      includeSpring        = true;

    private boolean                      autoReset            = true;

    private long                         cycleTime            = DEFAULT_CYCLE_TIME;
    private long                         pushTimeOut          = DEFAULT_PUSH_TIMEOUT;

    private ScheduledExecutorService     scheduler            = Executors.newScheduledThreadPool(1,
                                                                                                 new DaemonThreadFactory(
                                                                                                                         "DruidPullService-Scheduler"));

    private ScheduledFuture<?>           executeFuture        = null;

    private PushService(){
    }

    public static PushService getInstance() {
        return instance;
    }

    public void start() {
        executeFuture = scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                execute();
            }

        }, 0, cycleTime, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (executeFuture != null) {
            if (executeFuture.cancel(true)) {
                executeFuture = null;
            }
        }
    }

    protected void execute() {
        Map<String, Object> data = collectAndReset();
        String json = JSONUtils.toJSONString(data);
        push("data=" + json);
    }

    /**
     * @return
     */
    protected Map<String, Object> collectAndReset() {
        Map<String, Object> data = new HashMap<String, Object>();

        if (includeSql) {
            List<Map<String, Object>> dataSources = statManagerFacade.getDataSourceStatDataList(true);
            data.put("DataSources", dataSources);
        }

        if (includeWebApp) {
            List<Map<String, Object>> list = webAppStatManager.getWebAppStatData();
            data.put("WebApp", list);
        }

        if (includeWebUri) {
            List<Map<String, Object>> list = webAppStatManager.getURIStatData();
            data.put("WebURI", list);
        }

        if (includeWebSession) {
            List<Map<String, Object>> list = webAppStatManager.getSessionStatData();
            data.put("WebSession", list);
        }

        if (includeSpring) {
            List<Map<String, Object>> list = springStatManager.getMethodStatData();
            data.put("Spring", list);
        }

        if (autoReset) {
            statManagerFacade.resetAll();
        }
        return data;
    }

    protected void push(String data) {
        HttpClientUtils.post(serverUrl, data, pushTimeOut);
    }

    public long getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(long cycleTime) {
        this.cycleTime = cycleTime;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isIncludeSql() {
        return includeSql;
    }

    public void setIncludeSql(boolean includeSql) {
        this.includeSql = includeSql;
    }

    public boolean isIncludeWebApp() {
        return includeWebApp;
    }

    public void setIncludeWebApp(boolean includeWebApp) {
        this.includeWebApp = includeWebApp;
    }

    public boolean isIncludeWebUri() {
        return includeWebUri;
    }

    public void setIncludeWebUri(boolean includeWebUri) {
        this.includeWebUri = includeWebUri;
    }

    public boolean isIncludeWebSession() {
        return includeWebSession;
    }

    public void setIncludeWebSession(boolean includeWebSession) {
        this.includeWebSession = includeWebSession;
    }

    public boolean isIncludeSpring() {
        return includeSpring;
    }

    public void setIncludeSpring(boolean includeSpring) {
        this.includeSpring = includeSpring;
    }

    public boolean isAutoReset() {
        return autoReset;
    }

    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }
}
