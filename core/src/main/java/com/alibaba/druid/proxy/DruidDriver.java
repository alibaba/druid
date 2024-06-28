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
package com.alibaba.druid.proxy;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JMXUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDriver implements Driver, DruidDriverMBean {
    private static Log LOG; // lazy init

    private static final DruidDriver instance = new DruidDriver();

    private static final ConcurrentMap<String, DataSourceProxyImpl> proxyDataSources = new ConcurrentHashMap<String, DataSourceProxyImpl>(16, 0.75f, 1);
    private static final AtomicInteger dataSourceIdSeed = new AtomicInteger(0);
    private static final AtomicInteger sqlStatIdSeed = new AtomicInteger(0);

    public static final String DEFAULT_PREFIX = "jdbc:wrap-jdbc:";
    public static final String DRIVER_PREFIX = "driver=";
    public static final String PASSWORD_CALLBACK_PREFIX = "passwordCallback=";
    public static final String NAME_PREFIX = "name=";
    public static final String JMX_PREFIX = "jmx=";
    public static final String FILTERS_PREFIX = "filters=";

    private final AtomicLong connectCount = new AtomicLong(0);

    private String acceptPrefix = DEFAULT_PREFIX;

    private int majorVersion = 4;

    private int minorVersion;

    private static final String MBEAN_NAME = "com.alibaba.druid:type=DruidDriver";

    static {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                registerDriver(instance);
                return null;
            }
        });
    }

    public static boolean registerDriver(Driver driver) {
        try {
            DriverManager.registerDriver(driver);

            try {
                MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

                ObjectName objectName = new ObjectName(MBEAN_NAME);
                if (!mbeanServer.isRegistered(objectName)) {
                    mbeanServer.registerMBean(instance, objectName);
                }
            } catch (Throwable ex) {
                if (LOG == null) {
                    LOG = LogFactory.getLog(DruidDriver.class);
                }
                LOG.warn("register druid-driver mbean error", ex);
            }

            return true;
        } catch (Exception e) {
            if (LOG == null) {
                LOG = LogFactory.getLog(DruidDriver.class);
            }

            LOG.error("registerDriver error", e);
        }

        return false;
    }

    public DruidDriver() {
    }

    public static DruidDriver getInstance() {
        return instance;
    }

    public static int createDataSourceId() {
        return dataSourceIdSeed.incrementAndGet();
    }

    public static int createSqlStatId() {
        return sqlStatIdSeed.incrementAndGet();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }

        if (url.startsWith(acceptPrefix)) {
            return true;
        }

        return false;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        connectCount.incrementAndGet();

        DataSourceProxyImpl dataSource = getDataSource(url, info);

        return dataSource.connect(info);
    }

    /**
     * 参数定义： com.alibaba.druid.log.LogFilter=filter com.alibaba.druid.log.LogFilter.p1=prop-value
     * com.alibaba.druid.log.LogFilter.p2=prop-value
     *
     * @param url
     * @return
     * @throws SQLException
     */
    public static DataSourceProxyImpl getDataSource(String url, Properties info) throws SQLException {
        DataSourceProxyImpl dataSource = proxyDataSources.get(url);

        if (dataSource == null) {
            DataSourceProxyConfig config = parseConfig(url, info);

            Driver rawDriver = createDriver(config.getRawDriverClassName());

            DataSourceProxyImpl newDataSource = new DataSourceProxyImpl(rawDriver, config);

            {
                String property = System.getProperty("druid.filters");
                if (property != null && property.length() > 0) {
                    for (String filterItem : property.split(",")) {
                        FilterManager.loadFilter(config.getFilters(), filterItem);
                    }
                }
            }
            {
                int dataSourceId = createDataSourceId();
                newDataSource.setId(dataSourceId);

                for (Filter filter : config.getFilters()) {
                    filter.init(newDataSource);
                }
            }

            DataSourceProxy oldDataSource = proxyDataSources.putIfAbsent(url, newDataSource);
            if (oldDataSource == null) {
                if (config.isJmxOption()) {
                    JMXUtils.register("com.alibaba.druid:type=JdbcStat", JdbcStatManager.getInstance());
                }
            }

            dataSource = proxyDataSources.get(url);
        }
        return dataSource;
    }

    public static DataSourceProxyConfig parseConfig(String url, Properties info) throws SQLException {
        String restUrl = url.substring(DEFAULT_PREFIX.length());
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        int colonPos = -1;
        while ((colonPos = restUrl.indexOf(":")) != -1)
        {
            if (restUrl.startsWith("jdbc:")) {
                break;
            }
            if (restUrl.indexOf("=") == -1) {
                break;
            }
            String fragmentText = restUrl.substring(0, colonPos);
            int equalPos = fragmentText.indexOf("=");
            if (equalPos == -1)
            {
                continue;
            }
            String key = fragmentText.substring(0, equalPos + 1);
            String value = fragmentText.substring(equalPos + 1);
            if (StringUtils.equalsIgnoreCase(key, DRIVER_PREFIX))
            {
                config.setRawDriverClassName(value.trim());
            }
            else if (StringUtils.equalsIgnoreCase(key, FILTERS_PREFIX))
            {
                for (String filterItem : value.split(",")) {
                    FilterManager.loadFilter(config.getFilters(), filterItem);
                }
            }
            else if (StringUtils.equalsIgnoreCase(key, NAME_PREFIX))
            {
                config.setName(value);
            }
            else if (StringUtils.equalsIgnoreCase(key, JMX_PREFIX))
            {
                config.setJmxOption(value);
            }
            restUrl = restUrl.substring(colonPos + 1);
        }
        config.setRawUrl(restUrl);
        if (config.getRawDriverClassName() == null || config.getRawDriverClassName().isEmpty()) {
            String rawDriverClassname = JdbcUtils.getDriverClassName(restUrl);
            config.setRawDriverClassName(rawDriverClassname);
        }
        config.setUrl(url);
        return config;
    }

    public static Driver createDriver(final String className) throws SQLException {
        Class<?> rawDriverClass = Utils.loadClass(className);

        if (rawDriverClass == null) {
            throw new SQLException("jdbc-driver's class not found. '" + className + "'");
        }

        Driver rawDriver;
        try {
            rawDriver = (Driver) rawDriverClass.newInstance();
        } catch (InstantiationException e) {
            throw new SQLException("create driver instance error, driver className '" + className + "'", e);
        } catch (IllegalAccessException e) {
            throw new SQLException("create driver instance error, driver className '" + className + "'", e);
        }

        return rawDriver;
    }

    @Override
    public int getMajorVersion() {
        return this.majorVersion;
    }

    @Override
    public int getMinorVersion() {
        return this.minorVersion;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DataSourceProxyImpl dataSource = getDataSource(url, info);
        return dataSource.getRawDriver().getPropertyInfo(dataSource.getConfig().getRawUrl(), info);
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public long getConnectCount() {
        return connectCount.get();
    }

    public String getAcceptPrefix() {
        return acceptPrefix;
    }

    @Override
    public String[] getDataSourceUrls() {
        return proxyDataSources.keySet().toArray(new String[proxyDataSources.size()]);
    }

    public static ConcurrentMap<String, DataSourceProxyImpl> getProxyDataSources() {
        return proxyDataSources;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void resetStat() {
        connectCount.set(0);
    }

    @Override
    public String getDruidVersion() {
        return VERSION.getVersionNumber();
    }
}
