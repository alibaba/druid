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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

/**
 * A Thread trying to test all DataSource provided by HADataSource.
 * If a DataSource failed this test for 3 times (default value), it will be put into a blacklist.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceValidateThread implements Runnable {
    private final static Log LOG = LogFactory.getLog(RandomDataSourceValidateThread.class);
    private int checkingIntervalSeconds = 15;
    private int validationSleepSeconds = 0;
    private int blacklistThreshold = 3;
    private RandomDataSourceSelector selector;
    private ExecutorService checkExecutor = Executors.newFixedThreadPool(5);
    private Map<String, Integer> errorCounts = new ConcurrentHashMap<String, Integer>();

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
            sleepForNextValidation();
        }
    }

    private void sleepForNextValidation() {
        int errorCountBelowThreshold = 0;

        for (int count : errorCounts.values()) {
            if (count > 0 && count < blacklistThreshold && count > errorCountBelowThreshold) {
                errorCountBelowThreshold = count;
            }
        }

        int newSleepSeconds = checkingIntervalSeconds / (errorCountBelowThreshold + 1);
        if (newSleepSeconds < 1) {
            newSleepSeconds = 1;
        }
        try {
            LOG.debug("Sleep " + newSleepSeconds + " second(s) until next checking.");
            Thread.sleep(newSleepSeconds * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private void maintainBlacklist() {
        Map<String, DataSource> dataSourceMap = selector.getDataSourceMap();
        for (Map.Entry<String, Integer> e : errorCounts.entrySet()) {
            if (e.getValue() <= 0) {
                selector.removeBlacklist(dataSourceMap.get(e.getKey()));
            } else if (e.getValue() >= blacklistThreshold
                    && !selector.containInBlacklist(dataSourceMap.get(e.getKey()))) {
                LOG.warn("Adding " + e.getKey() + " to blacklist.");
                selector.addBlacklist(dataSourceMap.get(e.getKey()));
            }
        }
    }

    private void checkAllDataSources() {
        Map<String, DataSource> dataSourceMap = selector.getDataSourceMap();
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();

        for (final Map.Entry<String, DataSource> e : dataSourceMap.entrySet()) {
            if (!(e.getValue() instanceof DruidDataSource)) {
                continue;
            }

            if (selector.containInBlacklist(e.getValue())) {
                LOG.debug(e.getKey() + " is already in blacklist, skip.");
                continue;
            }

            tasks.add(new Callable<Boolean>() {
                @Override
                public Boolean call() {
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

                    return flag;
                }
            });
        }
        try {
            checkExecutor.invokeAll(tasks);
        } catch (Exception e) {
            LOG.warn("Exception occurred while checking DataSource.", e);
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
            LOG.debug("Validating " + name + " every " + checkingIntervalSeconds + " seconds.");
            conn = driver.connect(url, info);
            sleepBeforeValidation();
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

    private void sleepBeforeValidation() {
        if (validationSleepSeconds <= 0) {
            return;
        }
        try {
            LOG.debug("Sleep " + validationSleepSeconds + " second(s) before validation.");
            Thread.sleep(validationSleepSeconds * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public int getCheckingIntervalSeconds() {
        return checkingIntervalSeconds;
    }

    public void setCheckingIntervalSeconds(int checkingIntervalSeconds) {
        this.checkingIntervalSeconds = checkingIntervalSeconds;
    }

    public int getValidationSleepSeconds() {
        return validationSleepSeconds;
    }

    public void setValidationSleepSeconds(int validationSleepSeconds) {
        this.validationSleepSeconds = validationSleepSeconds;
    }

    public int getBlacklistThreshold() {
        return blacklistThreshold;
    }

    public void setBlacklistThreshold(int blacklistThreshold) {
        this.blacklistThreshold = blacklistThreshold;
    }
}
