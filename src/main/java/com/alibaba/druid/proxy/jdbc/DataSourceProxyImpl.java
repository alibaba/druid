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
package com.alibaba.druid.proxy.jdbc;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class DataSourceProxyImpl implements DataSourceProxy, DataSourceProxyImplMBean {

    private final Driver                rawDriver;

    private final DataSourceProxyConfig config;

    private long                        id;

    private final long                  createdTimeMillis = System.currentTimeMillis();

    private Properties                  properties;

    private String                      dbType;

    private final AtomicLong            connectionIdSeed  = new AtomicLong(10000);
    private final AtomicLong            statementIdSeed   = new AtomicLong(20000);
    private final AtomicLong            resultSetIdSeed   = new AtomicLong(50000);
    private final AtomicLong            metaDataIdSeed    = new AtomicLong(100000);
    private final AtomicLong            transactionIdSeed = new AtomicLong(0);

    private final JdbcDataSourceStat    dataSourceStat;

    public DataSourceProxyImpl(Driver rawDriver, DataSourceProxyConfig config){
        super();
        this.rawDriver = rawDriver;
        this.config = config;
        this.dbType = JdbcUtils.getDbType(config.getRawUrl(), config.getRawDriverClassName());
        this.dataSourceStat = new JdbcDataSourceStat(config.getName(), config.getUrl(), dbType);
    }

    public String getDbType() {
        return dbType;
    }

    public Driver getRawDriver() {
        return this.rawDriver;
    }

    public String getRawUrl() {
        return config.getRawUrl();
    }

    public ConnectionProxy connect(Properties info) throws SQLException {
        this.properties = info;

        PasswordCallback passwordCallback = this.config.getPasswordCallback();

        if (passwordCallback != null) {
            char[] chars = passwordCallback.getPassword();
            String password = new String(chars);
            info.put("password", password);
        }

        NameCallback userCallback = this.config.getUserCallback();
        if (userCallback != null) {
            String user = userCallback.getName();
            info.put("user", user);
        }

        FilterChain chain = new FilterChainImpl(this);
        return chain.connection_connect(info);
    }

    public DataSourceProxyConfig getConfig() {
        return config;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.config.getName();
    }

    @Override
    public String getUrl() {
        return config.getUrl();
    }

    public List<Filter> getProxyFilters() {
        return config.getFilters();
    }

    @Override
    public String[] getFilterClasses() {
        List<Filter> filterConfigList = config.getFilters();

        List<String> classes = new ArrayList<String>();
        for (Filter filter : filterConfigList) {
            classes.add(filter.getClass().getName());
        }

        return classes.toArray(new String[classes.size()]);
    }

    @Override
    public String getRawDriverClassName() {
        return config.getRawDriverClassName();
    }

    @Override
    public Date getCreatedTime() {
        return new Date(createdTimeMillis);
    }

    @Override
    public int getRawDriverMajorVersion() {
        return rawDriver.getMajorVersion();
    }

    @Override
    public int getRawDriverMinorVersion() {
        return rawDriver.getMinorVersion();
    }

    public String getDataSourceMBeanDomain() {
        String name = this.config.getName();
        if (name != null && name.length() != 0) {
            return name;
        }

        return "java.sql.dataSource_" + System.identityHashCode(this);
    }

    public String getProperties() {
        if (this.properties == null) {
            return null;
        }

        Properties properties = new Properties(this.properties);
        if (properties.contains("password")) {
            properties.put("password", "******");
        }
        return properties.toString();
    }

    public Properties getConnectProperties() {
        return properties;
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        JdbcDataSourceStat stat = this.getDataSourceStat();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", id);
        map.put("URL", this.getUrl());
        map.put("Name", this.getName());
        map.put("FilterClasses", getFilterClasses());
        map.put("CreatedTime", getCreatedTime());

        map.put("RawDriverClassName", getRawDriverClassName());
        map.put("RawUrl", getRawUrl());
        map.put("RawDriverMajorVersion", getRawDriverMajorVersion());
        map.put("RawDriverMinorVersion", getRawDriverMinorVersion());
        map.put("Properties", getProperties());

        if (stat != null) {
            map.put("ConnectionActiveCount", stat.getConnectionActiveCount());
            map.put("ConnectionActiveCountMax", stat.getConnectionStat().getActiveMax());
            map.put("ConnectionCloseCount", stat.getConnectionStat().getCloseCount());
            map.put("ConnectionCommitCount", stat.getConnectionStat().getCommitCount());
            map.put("ConnectionRollbackCount", stat.getConnectionStat().getRollbackCount());

            map.put("ConnectionConnectLastTime", stat.getConnectionStat().getConnectLastTime());
            map.put("ConnectionConnectErrorCount", stat.getConnectionStat().getConnectErrorCount());
            Throwable lastConnectionConnectError = stat.getConnectionStat().getConnectErrorLast();
            if (lastConnectionConnectError != null) {
                map.put("ConnectionConnectErrorLastTime", stat.getConnectionStat().getErrorLastTime());
                map.put("ConnectionConnectErrorLastMessage", lastConnectionConnectError.getMessage());
                map.put("ConnectionConnectErrorLastStackTrace", Utils.getStackTrace(lastConnectionConnectError));
            } else {
                map.put("ConnectionConnectErrorLastTime", null);
                map.put("ConnectionConnectErrorLastMessage", null);
                map.put("ConnectionConnectErrorLastStackTrace", null);
            }

            map.put("StatementCreateCount", stat.getStatementStat().getCreateCount());
            map.put("StatementPrepareCount", stat.getStatementStat().getPrepareCount());
            map.put("StatementPreCallCount", stat.getStatementStat().getPrepareCallCount());
            map.put("StatementExecuteCount", stat.getStatementStat().getExecuteCount());
            map.put("StatementRunningCount", stat.getStatementStat().getRunningCount());

            map.put("StatementConcurrentMax", stat.getStatementStat().getConcurrentMax());
            map.put("StatementCloseCount", stat.getStatementStat().getCloseCount());
            map.put("StatementErrorCount", stat.getStatementStat().getErrorCount());
            Throwable lastStatementError = stat.getStatementStat().getLastException();
            if (lastStatementError != null) {
                map.put("StatementLastErrorTime", stat.getStatementStat().getLastErrorTime());
                map.put("StatementLastErrorMessage", lastStatementError.getMessage());

                map.put("StatementLastErrorStackTrace", Utils.getStackTrace(lastStatementError));
            } else {
                map.put("StatementLastErrorTime", null);
                map.put("StatementLastErrorMessage", null);

                map.put("StatementLastErrorStackTrace", null);
            }
            map.put("StatementExecuteMillisTotal", stat.getStatementStat().getMillisTotal());
            map.put("StatementExecuteLastTime", stat.getStatementStat().getExecuteLastTime());
            map.put("ConnectionConnectingCount", stat.getConnectionStat().getConnectingCount());
            map.put("ResultSetCloseCount", stat.getResultSetStat().getCloseCount());

            map.put("ResultSetOpenCount", stat.getResultSetStat().getOpenCount());
            map.put("ResultSetOpenningCount", stat.getResultSetStat().getOpeningCount());
            map.put("ResultSetOpenningMax", stat.getResultSetStat().getOpeningMax());
            map.put("ResultSetFetchRowCount", stat.getResultSetStat().getFetchRowCount());
            map.put("ResultSetLastOpenTime", stat.getResultSetStat().getLastOpenTime());

            map.put("ResultSetErrorCount", stat.getResultSetStat().getErrorCount());
            map.put("ResultSetOpenningMillisTotal", stat.getResultSetStat().getAliveMillisTotal());
            map.put("ResultSetLastErrorTime", stat.getResultSetStat().getLastErrorTime());
            Throwable lastResultSetError = stat.getResultSetStat().getLastError();
            if (lastResultSetError != null) {
                map.put("ResultSetLastErrorMessage", lastResultSetError.getMessage());
                map.put("ResultSetLastErrorStackTrace", Utils.getStackTrace(lastResultSetError));
            } else {
                map.put("ResultSetLastErrorMessage", null);
                map.put("ResultSetLastErrorStackTrace", null);
            }

            map.put("ConnectionConnectCount", stat.getConnectionStat().getConnectCount());
            Throwable lastConnectionError = stat.getConnectionStat().getErrorLast();
            if (lastConnectionError != null) {
                map.put("ConnectionErrorLastMessage", lastConnectionError.getMessage());
                map.put("ConnectionErrorLastStackTrace", Utils.getStackTrace(lastConnectionError));
            } else {
                map.put("ConnectionErrorLastMessage", null);
                map.put("ConnectionErrorLastStackTrace", null);
            }
            map.put("ConnectionConnectMillisTotal", stat.getConnectionStat().getConnectMillis());
            map.put("ConnectionConnectingCountMax", stat.getConnectionStat().getConnectingMax());

            map.put("ConnectionConnectMillisMax", stat.getConnectionStat().getConnectMillisMax());
            map.put("ConnectionErrorLastTime", stat.getConnectionStat().getErrorLastTime());
            map.put("ConnectionAliveMillisMax", stat.getConnectionConnectAliveMillisMax());
            map.put("ConnectionAliveMillisMin", stat.getConnectionConnectAliveMillisMin());

            map.put("ConnectionHistogram", stat.getConnectionHistogramValues());
            map.put("StatementHistogram", stat.getStatementStat().getHistogramValues());
        } else {
            map.put("ConnectionActiveCount", null);
            map.put("ConnectionActiveCountMax", null);
            map.put("ConnectionCloseCount", null);
            map.put("ConnectionCommitCount", null);
            map.put("ConnectionRollbackCount", null);

            map.put("ConnectionConnectLastTime", null);
            map.put("ConnectionConnectErrorCount", null);
            map.put("ConnectionConnectErrorLastTime", null);
            map.put("ConnectionConnectErrorLastMessage", null);
            map.put("ConnectionConnectErrorLastStackTrace", null);

            map.put("StatementCreateCount", null);
            map.put("StatementPrepareCount", null);
            map.put("StatementPreCallCount", null);
            map.put("StatementExecuteCount", null);
            map.put("StatementRunningCount", null);

            map.put("StatementConcurrentMax", null);
            map.put("StatementCloseCount", null);
            map.put("StatementErrorCount", null);
            map.put("StatementLastErrorTime", null);
            map.put("StatementLastErrorMessage", null);

            map.put("StatementLastErrorStackTrace", null);
            map.put("StatementExecuteMillisTotal", null);
            map.put("ConnectionConnectingCount", null);
            map.put("StatementExecuteLastTime", null);
            map.put("ResultSetCloseCount", null);

            map.put("ResultSetOpenCount", null);
            map.put("ResultSetOpenningCount", null);
            map.put("ResultSetOpenningMax", null);
            map.put("ResultSetFetchRowCount", null);
            map.put("ResultSetLastOpenTime", null);

            map.put("ResultSetErrorCount", null);
            map.put("ResultSetOpenningMillisTotal", null);
            map.put("ResultSetLastErrorTime", null);
            map.put("ResultSetLastErrorMessage", null);
            map.put("ResultSetLastErrorStackTrace", null);

            map.put("ConnectionConnectCount", null);
            map.put("ConnectionErrorLastMessage", null);
            map.put("ConnectionErrorLastStackTrace", null);
            map.put("ConnectionConnectMillisTotal", null);
            map.put("ConnectionConnectingCountMax", null);

            map.put("ConnectionConnectMillisMax", null);
            map.put("ConnectionErrorLastTime", null);
            map.put("ConnectionAliveMillisMax", null);
            map.put("ConnectionAliveMillisMin", null);

            map.put("ConnectionHistogram", new long[0]);
            map.put("StatementHistogram", new long[0]);
        }

        return new CompositeDataSupport(JdbcStatManager.getDataSourceCompositeType(), map);
    }

    @Override
    public String getRawJdbcUrl() {
        return config.getRawUrl();
    }

    public long createConnectionId() {
        return connectionIdSeed.incrementAndGet();
    }

    public long createStatementId() {
        return statementIdSeed.getAndIncrement();
    }

    public long createResultSetId() {
        return resultSetIdSeed.getAndIncrement();
    }

    public long createMetaDataId() {
        return metaDataIdSeed.getAndIncrement();
    }

    @Override
    public long createTransactionId() {
        return transactionIdSeed.getAndIncrement();
    }

    public JdbcDataSourceStat getDataSourceStat() {
        return dataSourceStat;
    }

}
