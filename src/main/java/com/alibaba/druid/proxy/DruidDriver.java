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

import java.lang.management.ManagementFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JMXUtils;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDriver implements Driver, DruidDriverMBean {

    private static Log                                              LOG; // lazy init

    private final static DruidDriver                                instance                 = new DruidDriver();

    private final static ConcurrentMap<String, DataSourceProxyImpl> proxyDataSources         = new ConcurrentHashMap<String, DataSourceProxyImpl>(16, 0.75f, 1);
    private final static AtomicInteger                              dataSourceIdSeed         = new AtomicInteger(0);
    private final static AtomicInteger                              sqlStatIdSeed            = new AtomicInteger(0);

    public final static String                                      DEFAULT_PREFIX           = "jdbc:wrap-jdbc:";
    public final static String                                      DRIVER_PREFIX            = "driver=";
    public final static String                                      PASSWORD_CALLBACK_PREFIX = "passwordCallback=";
    public final static String                                      NAME_PREFIX              = "name=";
    public final static String                                      JMX_PREFIX               = "jmx=";
    public final static String                                      FILTERS_PREFIX           = "filters=";

    private final AtomicLong                                        connectCount             = new AtomicLong(0);

    private String                                                  acceptPrefix             = DEFAULT_PREFIX;

    private int                                                     majorVersion             = 4;

    private int                                                     minorVersion             = 0;

    private final static String                                     MBEAN_NAME               = "com.alibaba.druid:type=DruidDriver";

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

    public DruidDriver(){

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
    private DataSourceProxyImpl getDataSource(String url, Properties info) throws SQLException {
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

        if (restUrl.startsWith(DRIVER_PREFIX)) {
            int pos = restUrl.indexOf(':', DRIVER_PREFIX.length());
            String driverText = restUrl.substring(DRIVER_PREFIX.length(), pos);
            if (driverText.length() > 0) {
                config.setRawDriverClassName(driverText.trim());
            }
            restUrl = restUrl.substring(pos + 1);
        }

        if (restUrl.startsWith(FILTERS_PREFIX)) {
            int pos = restUrl.indexOf(':', FILTERS_PREFIX.length());
            String filtersText = restUrl.substring(FILTERS_PREFIX.length(), pos);
            for (String filterItem : filtersText.split(",")) {
                FilterManager.loadFilter(config.getFilters(), filterItem);
            }
            restUrl = restUrl.substring(pos + 1);
        }

        if (restUrl.startsWith(NAME_PREFIX)) {
            int pos = restUrl.indexOf(':', NAME_PREFIX.length());
            String name = restUrl.substring(NAME_PREFIX.length(), pos);
            config.setName(name);
            restUrl = restUrl.substring(pos + 1);
        }

        if (restUrl.startsWith(JMX_PREFIX)) {
            int pos = restUrl.indexOf(':', JMX_PREFIX.length());
            String jmxOption = restUrl.substring(JMX_PREFIX.length(), pos);
            config.setJmxOption(jmxOption);
            restUrl = restUrl.substring(pos + 1);
        }

        String rawUrl = restUrl;
        config.setRawUrl(rawUrl);

        if (config.getRawDriverClassName() == null) {
            String rawDriverClassname = JdbcUtils.getDriverClassName(rawUrl);
            config.setRawDriverClassName(rawDriverClassname);
        }

        config.setUrl(url);
        return config;
    }

    public Driver createDriver(String className) throws SQLException {
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
