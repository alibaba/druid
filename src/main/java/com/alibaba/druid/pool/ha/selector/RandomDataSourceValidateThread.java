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
package com.alibaba.druid.pool.ha.selector;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A Thread trying to test all DataSource provided by HADataSource.
 * If a DataSource failed this test for 3 times, it will be put into a blacklist.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceValidateThread implements Runnable {
    private final static Log LOG = LogFactory.getLog(RandomDataSourceValidateThread.class);
    private int sleepSeconds = 30;
    private int blacklistThreshold = 3;

    private RandomDataSourceSelector selector;
    private Map<String, Integer> errorCounts = new HashMap<String, Integer>();

    public RandomDataSourceValidateThread(RandomDataSourceSelector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            if (selector != null) {
                checkAllDataSources();
                maintainBlacklist();
            }
            try {
                Thread.sleep(sleepSeconds * 1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void maintainBlacklist() {
        Map<String, DataSource> dataSourceMap = selector.getDataSourceMap();
        for (Map.Entry<String, Integer> e : errorCounts.entrySet()) {
            if (e.getValue() <= 0) {
                selector.removeBlacklist(dataSourceMap.get(e.getKey()));
            } else if (e.getValue() >= blacklistThreshold) {
                LOG.warn("Adding " + e.getKey() + " to blacklist.");
                selector.addBlacklist(dataSourceMap.get(e.getKey()));
            }
        }
    }

    private void checkAllDataSources() {
        Map<String, DataSource> dataSourceMap = selector.getDataSourceMap();
        for (Map.Entry<String, DataSource> e : dataSourceMap.entrySet()) {
            if (!(e.getValue() instanceof DruidDataSource)) {
                continue;
            }
            boolean flag = check(e.getKey(), (DruidDataSource) e.getValue());

            if (flag) {
                errorCounts.put(e.getKey(), 0);
            } else {
                if (!errorCounts.containsKey(e.getKey())) {
                    errorCounts.put(e.getKey(), 0);
                }
                int count = errorCounts.get(e.getKey());
                errorCounts.put(e.getKey(), count + 1);
            }
        }
    }

    private boolean check(String name, DruidDataSource dataSource) {
        boolean result = true;
        Driver driver = dataSource.getRawDriver();
        Properties info = new Properties(dataSource.getConnectProperties());
        String username = dataSource.getUsername();
        String password = dataSource.getPassword();
        String url = dataSource.getUrl(); // We can't use rawUrl here, because the schema maybe set in url.
        Connection conn = null;

        if (info.getProperty("user") == null && username != null) {
            info.setProperty("user", username);
        }
        if (info.getProperty("password") == null && password != null) {
            info.setProperty("password", password);
        }
        try {
            LOG.debug("Validating " + name + " every " + sleepSeconds + " seconds.");
            conn = driver.connect(url, info);
            dataSource.validateConnection(conn);
        } catch (SQLException e) {
            LOG.warn("Validation FAILED for " + name + " with url [" + url + "] and username ["
                    + info.getProperty("user") + "]. Exception: " + e.getMessage());
            result = false;
        } finally {
            JdbcUtils.close(conn);
        }

        return result;
    }

    public void setSleepSeconds(int sleepSeconds) {
        this.sleepSeconds = sleepSeconds;
    }

    public void setBlacklistThreshold(int blacklistThreshold) {
        this.blacklistThreshold = blacklistThreshold;
    }
}
