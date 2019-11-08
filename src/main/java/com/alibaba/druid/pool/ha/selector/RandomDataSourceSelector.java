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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * A selector which uses java.util.Random to choose DataSource.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceSelector implements DataSourceSelector {
    private static final String PROP_PREFIX = "druid.ha.random.";
    public static final String PROP_CHECKING_INTERVAL = PROP_PREFIX + "checkingIntervalSeconds";
    public static final String PROP_RECOVERY_INTERVAL = PROP_PREFIX + "recoveryIntervalSeconds";
    public static final String PROP_VALIDATION_SLEEP = PROP_PREFIX + "validationSleepSeconds";
    public static final String PROP_BLACKLIST_THRESHOLD = PROP_PREFIX + "blacklistThreshold";

    private final static Log LOG = LogFactory.getLog(RandomDataSourceSelector.class);

    private Random random = new Random();
    private List<DataSource> blacklist = new CopyOnWriteArrayList<DataSource>();
    private HighAvailableDataSource highAvailableDataSource;
    private RandomDataSourceValidateThread validateThread;
    private RandomDataSourceRecoverThread recoverThread;

    private int checkingIntervalSeconds = 15;
    private int recoveryIntervalSeconds = 120;
    private int validationSleepSeconds = 0;
    private int blacklistThreshold = 3;

    public RandomDataSourceSelector(HighAvailableDataSource highAvailableDataSource) {
        this.highAvailableDataSource = highAvailableDataSource;
    }

    @Override
    public void init() {
        if (highAvailableDataSource == null) {
            LOG.warn("highAvailableDataSource is NULL!");
            return;
        }
        if (!highAvailableDataSource.isTestOnBorrow() && !highAvailableDataSource.isTestOnReturn()) {
            loadProperties();
            initThreads();
        } else {
            LOG.info("testOnBorrow or testOnReturn has been set to true, ignore validateThread");
        }
    }

    @Override
    public String getName() {
        return DataSourceSelectorEnum.RANDOM.getName();
    }

    @Override
    public DataSource get() {
        Map<String, DataSource> dataSourceMap = getDataSourceMap();
        if (dataSourceMap == null || dataSourceMap.isEmpty()) {
            return null;
        }

        Collection<DataSource> targetDataSourceSet;
        if (blacklist == null || blacklist.isEmpty() || blacklist.size() >= dataSourceMap.size()) {
            targetDataSourceSet = dataSourceMap.values();
        } else {
            targetDataSourceSet = new HashSet<DataSource>(dataSourceMap.values());
            for (DataSource b : blacklist) {
                targetDataSourceSet.remove(b);
            }
        }

        DataSource[] dataSources = targetDataSourceSet.toArray(new DataSource[] {});
        if (dataSources != null && dataSources.length > 0) {
            return dataSources[random.nextInt(targetDataSourceSet.size())];
        }
        return null;
    }

    @Override
    public void setTarget(String name) {
        // do nothing
    }

    public Map<String, DataSource> getDataSourceMap() {
        if (highAvailableDataSource != null) {
            return highAvailableDataSource.getDataSourceMap();
        }
        return new HashMap<String, DataSource>();
    }

    public List<DataSource> getBlacklist() {
        return blacklist;
    }

    public boolean containInBlacklist(DataSource dataSource) {
        return dataSource != null && blacklist.contains(dataSource);
    }

    public void addBlacklist(DataSource dataSource) {
        if (dataSource != null && !blacklist.contains(dataSource)) {
            blacklist.add(dataSource);
            if (dataSource instanceof DruidDataSource) {
                ((DruidDataSource) dataSource).setTestOnReturn(true);
            }
        }
    }

    public void removeBlacklist(DataSource dataSource) {
        if (containInBlacklist(dataSource)) {
            blacklist.remove(dataSource);
            if (dataSource instanceof DruidDataSource) {
                ((DruidDataSource) dataSource).setTestOnReturn(highAvailableDataSource.isTestOnReturn());
            }
        }
    }

    private void loadProperties() {
        checkingIntervalSeconds = loadInteger(PROP_CHECKING_INTERVAL, checkingIntervalSeconds);
        recoveryIntervalSeconds = loadInteger(PROP_RECOVERY_INTERVAL, recoveryIntervalSeconds);
        validationSleepSeconds = loadInteger(PROP_VALIDATION_SLEEP, validationSleepSeconds);
        blacklistThreshold = loadInteger(PROP_BLACKLIST_THRESHOLD, blacklistThreshold);
    }

    private int loadInteger(String name, int defaultValue) {
        if (name == null) {
            return defaultValue;
        }

        Properties properties = highAvailableDataSource.getConnectProperties();
        int value = defaultValue;
        try {
            if (properties.containsKey(name)) {
                value = Integer.parseInt(properties.getProperty(name));
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while parsing " + name, e);
        }
        return value;
    }

    private void initThreads() {
        if (validateThread == null) {
            validateThread = new RandomDataSourceValidateThread(this);
            validateThread.setCheckingIntervalSeconds(checkingIntervalSeconds);
            validateThread.setValidationSleepSeconds(validationSleepSeconds);
            validateThread.setBlacklistThreshold(blacklistThreshold);
        }
        new Thread(validateThread, "RandomDataSourceSelector-validate-thread").start();

        if (recoverThread == null) {
            recoverThread = new RandomDataSourceRecoverThread(this);
            recoverThread.setSleepSeconds(recoveryIntervalSeconds);
            recoverThread.setValidationSleepSeconds(validationSleepSeconds);
        }
        new Thread(recoverThread, "RandomDataSourceSelector-recover-thread").start();
    }

    public HighAvailableDataSource getHighAvailableDataSource() {
        return highAvailableDataSource;
    }

    public RandomDataSourceValidateThread getValidateThread() {
        return validateThread;
    }

    public void setValidateThread(RandomDataSourceValidateThread validateThread) {
        this.validateThread = validateThread;
    }

    public RandomDataSourceRecoverThread getRecoverThread() {
        return recoverThread;
    }

    public void setRecoverThread(RandomDataSourceRecoverThread recoverThread) {
        this.recoverThread = recoverThread;
    }

    public int getCheckingIntervalSeconds() {
        return checkingIntervalSeconds;
    }

    public void setCheckingIntervalSeconds(int checkingIntervalSeconds) {
        this.checkingIntervalSeconds = checkingIntervalSeconds;
    }

    public int getRecoveryIntervalSeconds() {
        return recoveryIntervalSeconds;
    }

    public void setRecoveryIntervalSeconds(int recoveryIntervalSeconds) {
        this.recoveryIntervalSeconds = recoveryIntervalSeconds;
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
