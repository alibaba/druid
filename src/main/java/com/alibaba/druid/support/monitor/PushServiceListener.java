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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

public class PushServiceListener implements ServletContextListener {

    private final static Log    LOG                = LogFactory.getLog(PushServiceListener.class);

    private final static String MONITOR_SERVER_URL = "monitorServerUrl";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PushService pushService = PushService.getInstance();
        String serverUrl = sce.getServletContext().getInitParameter(MONITOR_SERVER_URL);
        if (serverUrl == null) {
            LOG.error(MONITOR_SERVER_URL + " can't be null");
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(MONITOR_SERVER_URL + "= " + serverUrl);
        }
        pushService.setServerUrl(serverUrl);
        pushService.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

}
