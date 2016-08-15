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
package com.alibaba.druid.support.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.druid.support.monitor.MonitorClient;

public class MonitorClientContextListener implements ServletContextListener {

    private MonitorClient client;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        client = new MonitorClient();
        client.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (client != null) {
            client.stop();
            this.client = null;
        }
    }

}
