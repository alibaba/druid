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
package com.alibaba.druid.pool.ha;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.WrapperAdapter;
import com.alibaba.druid.pool.ha.node.FileNodeListener;
import com.alibaba.druid.pool.ha.node.NodeListener;
import com.alibaba.druid.pool.ha.node.PoolUpdater;
import com.alibaba.druid.pool.ha.selector.DataSourceSelector;
import com.alibaba.druid.pool.ha.selector.DataSourceSelectorEnum;
import com.alibaba.druid.pool.ha.selector.DataSourceSelectorFactory;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * DataSource class which contains multiple DataSource objects.
 *
 * @author DigitalSonic
 */
public class HighAvailableDataSource extends WrapperAdapter implements DataSource {
    private final static Log LOG = LogFactory.getLog(HighAvailableDataSource.class);
    private final static String DEFAULT_DATA_SOURCE_FILE = "ha-datasource.properties";

    // Properties copied from DruidAbstractDataSource BEGIN
    private String driverClassName;
    private Properties connectProperties = new Properties();
    private String connectionProperties = null;

    private int initialSize = DruidAbstractDataSource.DEFAULT_INITIAL_SIZE;
    private int maxActive = DruidAbstractDataSource.DEFAULT_MAX_ACTIVE_SIZE;
    private int minIdle = DruidAbstractDataSource.DEFAULT_MIN_IDLE;
    private long maxWait = DruidAbstractDataSource.DEFAULT_MAX_WAIT;

    private String validationQuery = DruidAbstractDataSource.DEFAULT_VALIDATION_QUERY;
    private int validationQueryTimeout = -1;
    private boolean testOnBorrow = DruidAbstractDataSource.DEFAULT_TEST_ON_BORROW;
    private boolean testOnReturn = DruidAbstractDataSource.DEFAULT_TEST_ON_RETURN;
    private boolean testWhileIdle = DruidAbstractDataSource.DEFAULT_WHILE_IDLE;

    private boolean poolPreparedStatements = false;
    private boolean sharePreparedStatements = false;
    private int maxPoolPreparedStatementPerConnectionSize = 10;

    private int queryTimeout;
    private int transactionQueryTimeout;

    private long timeBetweenEvictionRunsMillis = DruidAbstractDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    private long minEvictableIdleTimeMillis = DruidAbstractDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    private long maxEvictableIdleTimeMillis = DruidAbstractDataSource.DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS;
    private long phyTimeoutMillis = DruidAbstractDataSource.DEFAULT_PHY_TIMEOUT_MILLIS;
    private long timeBetweenConnectErrorMillis = DruidAbstractDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

    private boolean removeAbandoned;
    private long removeAbandonedTimeoutMillis = 300 * 1000;
    private boolean logAbandoned;

    private String filters;
    private List<Filter> proxyFilters = new CopyOnWriteArrayList<Filter>();
    private PrintWriter logWriter = new PrintWriter(System.out);
    // Properties copied from DruidAbstractDataSource END

    private Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
    private Set<String> blacklist = new CopyOnWriteArraySet<String>();
    private DataSourceSelector selector;
    private String dataSourceFile = DEFAULT_DATA_SOURCE_FILE;
    private String propertyPrefix = "";
    private int poolPurgeIntervalSeconds = PoolUpdater.DEFAULT_INTERVAL;
    private boolean allowEmptyPoolWhenUpdate = false;

    private volatile boolean inited = false;

    private PoolUpdater poolUpdater = new PoolUpdater(this);
    private NodeListener nodeListener;

    public void init() {
        if (inited) {
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            if (dataSourceMap == null || dataSourceMap.isEmpty()) {
                poolUpdater.setIntervalSeconds(poolPurgeIntervalSeconds);
                poolUpdater.setAllowEmptyPool(allowEmptyPoolWhenUpdate);
                poolUpdater.init();
                createNodeMap();
            }
            if (selector == null) {
                setSelector(DataSourceSelectorEnum.RANDOM.getName());
            }
            if (dataSourceMap == null || dataSourceMap.isEmpty()) {
                LOG.warn("There is NO DataSource available!!! Please check your configuration.");
            }
            inited = true;
        }
    }

    public void close() {
        destroy();
    }

    public void destroy() {
        if (nodeListener != null) {
            nodeListener.destroy();
        }
        if (poolUpdater != null) {
            poolUpdater.destroy();
        }
        if (selector != null) {
            selector.destroy();
        }
        if (dataSourceMap == null || dataSourceMap.isEmpty()) {
            return;
        }
        for (DataSource dataSource : dataSourceMap.values()) {
            if (dataSource instanceof DruidDataSource) {
                ((DruidDataSource) dataSource).close();
            }
        }
    }

    public void setTargetDataSource(String targetName) {
        selector.setTarget(targetName);
    }

    @Override
    public Connection getConnection() throws SQLException {
        init();
        DataSource dataSource = selector.get();
        if (dataSource == null) {
            LOG.warn("Can NOT obtain DataSource, return null.");
            return null;
        }
        return dataSource.getConnection();
    }

    public String getDataSourceFile() {
        return dataSourceFile;
    }

    public void setDataSourceFile(String dataSourceFile) {
        this.dataSourceFile = dataSourceFile;
    }

    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

    public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        if (dataSourceMap != null) {
            this.dataSourceMap = dataSourceMap;
        }
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public Map<String, DataSource> getAvailableDataSourceMap() {
        Map<String, DataSource> map = new ConcurrentHashMap<String, DataSource>(this.dataSourceMap);
        for (String n : blacklist) {
            if (map.containsKey(n)) {
                map.remove(n);
            }
        }
        return map;
    }

    public void addBlackList(String name) {
        if (dataSourceMap.containsKey(name)) {
            blacklist.add(name);
        } else {
            LOG.info("Key " + name + " is NOT existed, ignore it.");
        }
    }

    public void removeBlackList(String name) {
        blacklist.remove(name);
    }

    public boolean isInBlackList(String name) {
        return blacklist.contains(name);
    }

    public void setSelector(String name) {
        DataSourceSelector selector = DataSourceSelectorFactory.getSelector(name, this);
        if (selector != null) {
            selector.init();
            setDataSourceSelector(selector);
        }
    }

    public String getSelector() {
        return selector == null ? null : selector.getName();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by HighAvailableDataSource.");
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;

        // COPIED FROM DruidAbstractDataSource.setConnectionProperties()

        if (connectionProperties == null || connectionProperties.trim().length() == 0) {
            setConnectProperties(null);
            return;
        }

        String[] entries = connectionProperties.split(";");
        Properties properties = new Properties();
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            if (entry.length() > 0) {
                int index = entry.indexOf('=');
                if (index > 0) {
                    String name = entry.substring(0, index);
                    String value = entry.substring(index + 1);
                    properties.setProperty(name, value);
                } else {
                    // no value is empty string which is how java.util.Properties works
                    properties.setProperty(entry, "");
                }
            }
        }

        setConnectProperties(properties);
    }

    public void setConnectProperties(Properties connectProperties) {
        if (connectProperties == null) {
            connectProperties = new Properties();
        }

        if (this.connectProperties != null) {
            this.connectProperties.putAll(connectProperties);
        } else {
            this.connectProperties = connectProperties;
        }
    }

    private void createNodeMap() {
        if (nodeListener == null) {
            // Compatiable with the old version.
            // Create a FileNodeListener to watch the dataSourceFile.
            FileNodeListener listener = new FileNodeListener();
            listener.setFile(dataSourceFile);
            listener.setPrefix(propertyPrefix);
            nodeListener = listener;
        }
        nodeListener.setObserver(poolUpdater);
        nodeListener.init();
        nodeListener.update(); // Do update in the current Thread at the startup
    }

    public boolean isAllowEmptyPoolWhenUpdate() {
        return allowEmptyPoolWhenUpdate;
    }

    public void setAllowEmptyPoolWhenUpdate(boolean allowEmptyPoolWhenUpdate) {
        this.allowEmptyPoolWhenUpdate = allowEmptyPoolWhenUpdate;
    }

    // Getters & Setters

    public int getPoolPurgeIntervalSeconds() {
        return poolPurgeIntervalSeconds;
    }

    public void setPoolPurgeIntervalSeconds(int poolPurgeIntervalSeconds) {
        this.poolPurgeIntervalSeconds = poolPurgeIntervalSeconds;
    }

    public NodeListener getNodeListener() {
        return nodeListener;
    }

    public void setNodeListener(NodeListener nodeListener) {
        this.nodeListener = nodeListener;
    }

    public DataSourceSelector getDataSourceSelector() {
        return this.selector;
    }

    public void setDataSourceSelector(DataSourceSelector dataSourceSelector) {
        this.selector = dataSourceSelector;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Properties getConnectProperties() {
        return connectProperties;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public boolean isSharePreparedStatements() {
        return sharePreparedStatements;
    }

    public void setSharePreparedStatements(boolean sharePreparedStatements) {
        this.sharePreparedStatements = sharePreparedStatements;
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public int getTransactionQueryTimeout() {
        return transactionQueryTimeout;
    }

    public void setTransactionQueryTimeout(int transactionQueryTimeout) {
        this.transactionQueryTimeout = transactionQueryTimeout;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }

    public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }

    public long getPhyTimeoutMillis() {
        return phyTimeoutMillis;
    }

    public void setPhyTimeoutMillis(long phyTimeoutMillis) {
        this.phyTimeoutMillis = phyTimeoutMillis;
    }

    public long getTimeBetweenConnectErrorMillis() {
        return timeBetweenConnectErrorMillis;
    }

    public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public long getRemoveAbandonedTimeoutMillis() {
        return removeAbandonedTimeoutMillis;
    }

    public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public List<Filter> getProxyFilters() {
        return proxyFilters;
    }

    public void setProxyFilters(List<Filter> proxyFilters) {
        this.proxyFilters = proxyFilters;
    }
}
