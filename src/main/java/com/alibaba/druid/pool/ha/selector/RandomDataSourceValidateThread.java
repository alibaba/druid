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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;

/**
 * A Thread trying to test all DataSource provided by HADataSource.
 * If a DataSource failed this test for 3 times (default value), it will be put into a blacklist.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceValidateThread implements Runnable {
    public static final int DEFAULT_CHECKING_INTERVAL_SECONDS = 10;
    public static final int DEFAULT_BLACKLIST_THRESHOLD = 3;

    private final static Log LOG = LogFactory.getLog(RandomDataSourceValidateThread.class);
    private static Map<String, Long> successTimes = new ConcurrentHashMap<String, Long>();

    private int checkingIntervalSeconds = DEFAULT_CHECKING_INTERVAL_SECONDS; // This value should NOT be too small.
    private int validationSleepSeconds = 0;
    private int blacklistThreshold = DEFAULT_BLACKLIST_THRESHOLD;
    private RandomDataSourceSelector selector;
    private ExecutorService checkExecutor = Executors.newFixedThreadPool(5);
    private Map<String, Integer> errorCounts = new ConcurrentHashMap<String, Integer>();
    private Map<String, Long> lastCheckTimes = new ConcurrentHashMap<String, Long>();

    /**
     * Provide a static method to record the last success time of a DataSource
     */
    public static void logSuccessTime(DataSourceProxy dataSource) {
        if (dataSource != null && !StringUtils.isEmpty(dataSource.getName())) {
            String name = dataSource.getName();
            long time = System.currentTimeMillis();
            LOG.debug("Log successTime [" + time + "] for " + name);
            successTimes.put(name, time);
        }
    }

    public RandomDataSourceValidateThread(RandomDataSourceSelector selector) {
        LOG.debug("Create a RandomDataSourceValidateThread, hashCode=" + this.hashCode());
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            if (selector != null) {
                checkAllDataSources();
                maintainBlacklist();
                cleanup();
            } else {
                break;
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
            LOG.debug("[RandomDataSourceValidateThread@" + hashCode() + "] Sleep " + newSleepSeconds
                    + " second(s) until next checking.");
            Thread.sleep(newSleepSeconds * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private void cleanup() {
        Map<String, DataSource> dataSourceMap = selector.getFullDataSourceMap();
        Set<String> dataSources = new HashSet<String>();
        for (DataSource ds : dataSourceMap.values()) {
            if (ds instanceof DruidDataSource) {
                dataSources.add(((DruidDataSource) ds).getName());
            }
        }
        cleanupMap(successTimes, dataSources);
        cleanupMap(errorCounts, dataSources);
        cleanupMap(lastCheckTimes, dataSources);
    }

    private void cleanupMap(Map<String, ?> mapToClean, Set<String> keys) {
        Set<String> keysToRemove = new HashSet<String>();
        for (String k : mapToClean.keySet()) {
            if (!keys.contains(k)) {
                keysToRemove.add(k);
            }
        }
        for (String k : keysToRemove) {
            mapToClean.remove(k);
        }
    }

    private void maintainBlacklist() {
        Map<String, DataSource> dataSourceMap = selector.getFullDataSourceMap();
        for (String name : errorCounts.keySet()) {
            Integer count = errorCounts.get(name);
            DataSource dataSource = dataSourceMap.get(name);
            if (dataSource == null) {
                for (DataSource ds : dataSourceMap.values()) {
                    if (ds instanceof DruidDataSource
                            && name.equals(((DruidDataSource) ds).getName())) {
                        dataSource = ds;
                    }
                }
            }
            if (count == null || count <= 0) {
                selector.removeBlacklist(dataSource);
            } else if (count != null && count >= blacklistThreshold
                    && !selector.containInBlacklist(dataSource)) {
                LOG.warn("Adding " + name + " to blacklist.");
                selector.addBlacklist(dataSource);
            }
        }
    }

    private void checkAllDataSources() {
        Map<String, DataSource> dataSourceMap = selector.getFullDataSourceMap();
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        LOG.debug("Checking all DataSource(s).");
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
                    DruidDataSource dataSource = (DruidDataSource) e.getValue();
                    String name = dataSource.getName();

                    if (isSkipChecking(dataSource)) {
                        LOG.debug("Skip checking DataSource[" + name + "] this time.");
                        return true;
                    }

                    LOG.debug("Start checking " + name + ".");
                    boolean flag = check(dataSource);

                    if (flag) {
                        logSuccessTime(dataSource);
                        errorCounts.put(name, 0);
                    } else {
                        if (!errorCounts.containsKey(name)) {
                            errorCounts.put(name, 0);
                        }
                        int count = errorCounts.get(name);
                        errorCounts.put(name, count + 1);
                    }
                    lastCheckTimes.put(name, System.currentTimeMillis());

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

    private boolean isSkipChecking(DruidDataSource dataSource) {
        String name = dataSource.getName();
        Long lastSuccessTime = successTimes.get(dataSource.getName());
        Long lastCheckTime = lastCheckTimes.get(dataSource.getName());
        long currentTime = System.currentTimeMillis();
        LOG.debug("DataSource=" + name + ", lastSuccessTime=" + lastSuccessTime
                + ", lastCheckTime=" + lastCheckTime + ", currentTime=" + currentTime);
        return lastSuccessTime != null && lastCheckTime != null
                && (currentTime - lastSuccessTime) <= checkingIntervalSeconds * 1000
                && (currentTime - lastCheckTime) <= 5 * checkingIntervalSeconds * 1000
                && (!errorCounts.containsKey(name) || errorCounts.get(name) < 1);
    }

    private boolean check(DruidDataSource dataSource) {
        boolean result = true;
        String name = dataSource.getName();
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
            LOG.debug("[RandomDataSourceValidateThread@" + this.hashCode() + "] Validating " + name + " every " + checkingIntervalSeconds + " seconds.");
            conn = driver.connect(url, info);
            sleepBeforeValidation();
            dataSource.validateConnection(conn);
        } catch (Exception e) {
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

    public RandomDataSourceSelector getSelector() {
        return selector;
    }

    public void setSelector(RandomDataSourceSelector selector) {
        this.selector = selector;
    }
}
