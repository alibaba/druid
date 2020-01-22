/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.pool.ha.PropertiesUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A NodeList that monitors the change of a file.
 *
 * @author DigitalSonic
 */
public class FileNodeListener extends NodeListener {
    private final static Log LOG = LogFactory.getLog(FileNodeListener.class);

    private String file = null;
    private String prefix = "";
    private int intervalSeconds = 60;
    private ScheduledExecutorService executor;

    /**
     * Start a Scheduler to check the specified file.
     *
     * @see #setIntervalSeconds(int)
     * @see #update()
     */
    @Override
    public void init() {
        super.init();
        if (intervalSeconds <= 0) {
            intervalSeconds = 60;
        }
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    update();
                } catch (Exception e) {
                    LOG.error("Can NOT update the node list.", e);
                }
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Load the properties file and diff with the stored Properties.
     *
     * @return A List of the modification
     */
    @Override
    public List<NodeEvent> refresh() {
        Properties originalProperties = PropertiesUtils.loadProperties(file);
        List<String> nameList = PropertiesUtils.loadNameList(originalProperties, prefix);
        Properties properties = new Properties();
        for (String n : nameList) {
            String url = originalProperties.getProperty(n + ".url");
            String username = originalProperties.getProperty(n + ".username");
            String password = originalProperties.getProperty(n + ".password");
            if (url == null || url.isEmpty()
                    || username == null || username.isEmpty()
                    || password == null || password.isEmpty()) {
                continue;
            }
            properties.setProperty(n + ".url", url);
            properties.setProperty(n + ".username", username);
            properties.setProperty(n + ".password", password);
        }

        List<NodeEvent> events = NodeEvent.getEventListFromProperties(getProperties(), properties);
        if (events != null && !events.isEmpty()) {
            setProperties(properties);
        }
        return events;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
