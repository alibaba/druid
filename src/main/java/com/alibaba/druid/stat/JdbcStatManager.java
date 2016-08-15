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
package com.alibaba.druid.stat;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public final class JdbcStatManager implements JdbcStatManagerMBean {

    private final AtomicLong                  sqlIdSeed      = new AtomicLong(1000);

    private final static JdbcStatManager      instance       = new JdbcStatManager();

    private final JdbcConnectionStat          connectionStat = new JdbcConnectionStat();
    private final JdbcResultSetStat           resultSetStat  = new JdbcResultSetStat();
    private final JdbcStatementStat           statementStat  = new JdbcStatementStat();

    private final AtomicLong                  resetCount     = new AtomicLong();

    public final ThreadLocal<JdbcStatContext> contextLocal   = new ThreadLocal<JdbcStatContext>();

    private JdbcStatManager(){

    }

    public JdbcStatContext getStatContext() {
        return contextLocal.get();
    }

    public void setStatContext(JdbcStatContext context) {
        contextLocal.set(context);
    }

    public JdbcStatContext createStatContext() {
        return new JdbcStatContext();
    }

    public long generateSqlId() {
        return sqlIdSeed.incrementAndGet();
    }

    public static JdbcStatManager getInstance() {
        return instance;
    }

    public JdbcStatementStat getStatementStat() {
        return statementStat;
    }

    public JdbcResultSetStat getResultSetStat() {
        return resultSetStat;
    }

    public JdbcConnectionStat getConnectionStat() {
        return connectionStat;
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getDataSourceCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] {
                // 0 - 4
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                new ArrayType<SimpleType<String>>(SimpleType.STRING, false), //
                SimpleType.DATE, //

                // 5 - 9
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.STRING, //

                // 10 - 14
                SimpleType.LONG, //
                SimpleType.INTEGER, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                // 14 - 19
                SimpleType.DATE, //
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.STRING, //
                SimpleType.STRING, //

                // 20 - 24
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.INTEGER, //

                // 25 - 29
                SimpleType.INTEGER, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.STRING, //

                // 30 - 34
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.INTEGER, //
                SimpleType.DATE, //
                SimpleType.LONG, //

                // 35 - 39
                SimpleType.LONG, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.LONG, //
                SimpleType.DATE,

                // 40 - 44
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.STRING, //
                SimpleType.STRING,

                // 45 - 49
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.INTEGER, //

                // 50 - 54
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                new ArrayType<Long>(SimpleType.LONG, true), //

                // 55 -
                new ArrayType<Long>(SimpleType.LONG, true)
        //
        //
        };

        String[] indexNames = {
                // 0 - 4
                "ID",
                "URL",
                "Name",
                "FilterClasses",
                "CreatedTime", //

                // 5 - 9
                "RawUrl",
                "RawDriverClassName",
                "RawDriverMajorVersion",
                "RawDriverMinorVersion",
                "Properties" //
                ,

                // 10 - 14
                "ConnectionActiveCount",
                "ConnectionActiveCountMax",
                "ConnectionCloseCount",
                "ConnectionCommitCount",
                "ConnectionRollbackCount" //
                ,
                // 15 - 19
                "ConnectionConnectLastTime",
                "ConnectionConnectErrorCount",
                "ConnectionConnectErrorLastTime",
                "ConnectionConnectErrorLastMessage",
                "ConnectionConnectErrorLastStackTrace" //
                ,
                // 20 - 24
                "StatementCreateCount",
                "StatementPrepareCount",
                "StatementPreCallCount",
                "StatementExecuteCount",
                "StatementRunningCount" //
                ,

                // 25 - 29
                "StatementConcurrentMax",
                "StatementCloseCount",
                "StatementErrorCount",
                "StatementLastErrorTime",
                "StatementLastErrorMessage" //
                ,
                // 30 - 34
                "StatementLastErrorStackTrace", "StatementExecuteMillisTotal", "ConnectionConnectingCount",
                "StatementExecuteLastTime",
                "ResultSetCloseCount" //
                ,
                // 35 -39
                "ResultSetOpenCount", "ResultSetOpenningCount", "ResultSetOpenningMax", "ResultSetFetchRowCount",
                "ResultSetLastOpenTime" //
                ,
                // 40 - 44
                "ResultSetErrorCount", //
                "ResultSetOpenningMillisTotal", //
                "ResultSetLastErrorTime", //
                "ResultSetLastErrorMessage", //
                "ResultSetLastErrorStackTrace", //

                // 45 - 49
                "ConnectionConnectCount", //
                "ConnectionErrorLastMessage", //
                "ConnectionErrorLastStackTrace", //
                "ConnectionConnectMillisTotal", //
                "ConnectionConnectingCountMax",

                // 50 - 54
                "ConnectionConnectMillisMax", //
                "ConnectionErrorLastTime", //
                "ConnectionAliveMillisMax", //
                "ConnectionAliveMillisMin", //
                "ConnectionHistogram", //

                // 55 -
                "StatementHistogram",
        //
        };

        COMPOSITE_TYPE = new CompositeType("DataSourceStatistic", "DataSource Statistic", indexNames,
                indexNames, indexTypes);

        return COMPOSITE_TYPE;
    }

    @Override
    public TabularData getDataSourceList() throws JMException {
        CompositeType rowType = getDataSourceCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("DataSourceStat", "DataSourceStat", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        {
            final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getProxyDataSources();
            for (DataSourceProxyImpl dataSource : dataSources.values()) {
                data.put(dataSource.getCompositeData());
            }
        }

        final Set<DruidDataSource> dataSources = DruidDataSourceStatManager.getDruidDataSourceInstances();
        for (DruidDataSource dataSource : dataSources) {
            data.put(dataSource.getCompositeData());
        }

        return data;
    }

    @Override
    public TabularData getSqlList() throws JMException {
        CompositeType rowType = JdbcSqlStat.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("SqlListStatistic", "SqlListStatistic", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        JdbcDataSourceStat globalStat = JdbcDataSourceStat.getGlobal();
        if (globalStat != null) {
            Map<String, JdbcSqlStat> statMap = globalStat.getSqlStatMap();
            for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
                if (entry.getValue().getExecuteCount() == 0 && entry.getValue().getRunningCount() == 0) {
                    continue;
                }

                Map<String, Object> map = entry.getValue().getData();
                map.put("URL", globalStat.getUrl());
                data.put(new CompositeDataSupport(JdbcSqlStat.getCompositeType(), map));
            }
        }

        for (DataSourceProxyImpl dataSource : DruidDriver.getProxyDataSources().values()) {
            JdbcDataSourceStat druidDataSourceStat = dataSource.getDataSourceStat();

            if (druidDataSourceStat == globalStat) {
                continue;
            }

            Map<String, JdbcSqlStat> statMap = druidDataSourceStat.getSqlStatMap();
            for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
                if (entry.getValue().getExecuteCount() == 0 && entry.getValue().getRunningCount() == 0) {
                    continue;
                }

                Map<String, Object> map = entry.getValue().getData();
                map.put("URL", dataSource.getUrl());
                data.put(new CompositeDataSupport(JdbcSqlStat.getCompositeType(), map));
            }
        }

        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            JdbcDataSourceStat druidDataSourceStat = dataSource.getDataSourceStat();

            if (druidDataSourceStat == globalStat) {
                continue;
            }

            Map<String, JdbcSqlStat> statMap = druidDataSourceStat.getSqlStatMap();
            for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
                if (entry.getValue().getExecuteCount() == 0 && entry.getValue().getRunningCount() == 0) {
                    continue;
                }

                Map<String, Object> map = entry.getValue().getData();
                map.put("URL", dataSource.getUrl());
                data.put(new CompositeDataSupport(JdbcSqlStat.getCompositeType(), map));
            }
        }

        return data;
    }

    public TabularData getConnectionList() throws JMException {
        CompositeType rowType = JdbcConnectionStat.Entry.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("ConnectionList", "ConnectionList", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getProxyDataSources();
        for (DataSourceProxyImpl dataSource : dataSources.values()) {
            JdbcDataSourceStat dataSourceStat = dataSource.getDataSourceStat();
            ConcurrentMap<Long, JdbcConnectionStat.Entry> connections = dataSourceStat.getConnections();
            for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : connections.entrySet()) {
                data.put(entry.getValue().getCompositeData());
            }
        }

        for (DruidDataSource instance : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            JdbcDataSourceStat dataSourceStat = instance.getDataSourceStat();
            ConcurrentMap<Long, JdbcConnectionStat.Entry> connections = dataSourceStat.getConnections();
            for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : connections.entrySet()) {
                data.put(entry.getValue().getCompositeData());
            }
        }

        return data;
    }

    @Override
    public void reset() {
        resetCount.incrementAndGet();

        connectionStat.reset();
        statementStat.reset();
        resultSetStat.reset();

        final ConcurrentMap<String, DataSourceProxyImpl> dataSources = DruidDriver.getProxyDataSources();
        for (DataSourceProxyImpl dataSource : dataSources.values()) {
            dataSource.getDataSourceStat().reset();
        }

        for (DruidDataSource instance : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            instance.getDataSourceStat().reset();
        }
    }

    @Override
    public long getResetCount() {
        return resetCount.get();
    }
}
