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
package com.alibaba.druid.stat;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;

public class JdbcStatManager implements JdbcStatManagerMBean {

    private final AtomicLong                                sqlIdSeed      = new AtomicLong(1000);

    private final static JdbcStatManager                    instance       = new JdbcStatManager();

    private final JdbcConnectionStat                        connectionStat = new JdbcConnectionStat();
    private final JdbcResultSetStat                         resultSetStat  = new JdbcResultSetStat();
    private final JdbcStatementStat                         statementStat  = new JdbcStatementStat();

    private final ConcurrentMap<String, JdbcDataSourceStat> dataSources    = new ConcurrentHashMap<String, JdbcDataSourceStat>();

    private final AtomicLong                                resetCount     = new AtomicLong();

    public final ThreadLocal<JdbcStatContext>               contextLocal   = new ThreadLocal<JdbcStatContext>();

    private JdbcStatManager(){

    }

    public ConcurrentMap<String, JdbcDataSourceStat> getDataSources() {
        return dataSources;
    }

    public JdbcStatContext getStatContext() {
        return contextLocal.get();
    }

    public void setStatContext(JdbcStatContext context) {
        contextLocal.set(context);
    }

    public JdbcStatContext createStatContext() {
        JdbcStatContext context = new JdbcStatContext();

        context.setTraceEnable(JdbcTraceManager.getInstance().isTraceEnable());

        return context;
    }

    public long generateSqlId() {
        return sqlIdSeed.incrementAndGet();
    }

    public static final JdbcStatManager getInstance() {
        return instance;
    }

    public JdbcStatementStat getStatementStat() {
        return statementStat;
    }

    public JdbcResultSetStat getResultSetStat() {
        return resultSetStat;
    }

    public JdbcConnectionStat getConnectionstat() {
        return connectionStat;
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getDataSourceCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] { //
        SimpleType.LONG, SimpleType.STRING, SimpleType.STRING,
                new ArrayType<SimpleType<String>>(SimpleType.STRING, false), SimpleType.DATE, //
                SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.STRING //
                , SimpleType.LONG, SimpleType.INTEGER, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.DATE, SimpleType.LONG, SimpleType.DATE, SimpleType.STRING, SimpleType.STRING //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.INTEGER //
                , SimpleType.INTEGER, SimpleType.LONG, SimpleType.LONG, SimpleType.DATE, SimpleType.STRING //
                , SimpleType.STRING, SimpleType.LONG, SimpleType.INTEGER, SimpleType.DATE, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.LONG, SimpleType.DATE //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.DATE, SimpleType.STRING, SimpleType.STRING //
                , SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, SimpleType.INTEGER //
                , SimpleType.LONG, SimpleType.DATE, SimpleType.LONG, SimpleType.LONG //
                //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
                , SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG //
        //
        //
        };

        String[] indexNames = {
                "ID",
                "URL",
                "Name",
                "FilterClasses",
                "CreatedTime", //
                "RawUrl",
                "RawDriverClassName",
                "RawDriverMajorVersion",
                "RawDriverMinorVersion",
                "Properties" //
                ,
                "ConnectionActiveCount",
                "ConnectionActiveCountMax",
                "ConnectionCloseCount",
                "ConnectionCommitCount",
                "ConnectionRollbackCount" //
                ,
                "ConnectionConnectLastTime",
                "ConnectionConnectErrorCount",
                "ConnectionConnectErrorLastTime",
                "ConnectionConnectErrorLastMessage",
                "ConnectionConnectErrorLastStackTrace" //
                ,
                "StatementCreateCount",
                "StatementPrepareCount",
                "StatementPreCallCount",
                "StatementExecuteCount",
                "StatementRunningCount" //
                ,
                "StatementConcurrentMax",
                "StatementCloseCount",
                "StatementErrorCount",
                "StatementLastErrorTime",
                "StatementLastErrorMessage" //
                ,
                "StatementLastErrorStackTrace",
                "StatementExecuteMillisTotal",
                "ConnectionConnectingCount",
                "StatementExecuteLastTime",
                "ResultSetCloseCount" //
                ,
                "ResultSetOpenCount",
                "ResultSetOpenningCount",
                "ResultSetOpenningMax",
                "ResultSetFetchRowCount",
                "ResultSetLastOpenTime" //
                ,
                "ResultSetErrorCount",
                "ResultSetOpenningMillisTotal",
                "ResultSetLastErrorTime",
                "ResultSetLastErrorMessage",
                "ResultSetLastErrorStackTrace",
                "ConnectionConnectCount",
                "ConnectionErrorLastMessage",
                "ConnectionErrorLastStackTrace",
                "ConnectionConnectMillisTotal",
                "ConnectionConnectingCountMax" //
                ,
                "ConnectionConnectMillisMax",
                "ConnectionErrorLastTime",
                "ConnectionAliveMillisMax",
                "ConnectionAliveMillisMin" //
                //
                ,
                "ConnectionCount_Alive_0_1_Seconds",
                "ConnectionCount_Alive_1_5_Seconds",
                "ConnectionCount_Alive_5_10_Seconds",
                "ConnectionCount_Alive_10_30_Seconds",
                "ConnectionCount_Alive_30_60_Seconds" //
                ,
                "ConnectionCount_Alive_1_5_Minutes",
                "ConnectionCount_Alive_5_10_Minutes",
                "ConnectionCount_Alive_10_30_Minutes",
                "ConnectionCount_Alive_30_60_Minutes",
                "ConnectionCount_Alive_1_6_Hours" //
                ,
                "ConnectionCount_Alive_6_24_Hours",
                "ConnectionCount_Alive_1_7_Day",
                "ConnectionCount_Alive_7_30_Day",
                "ConnectionCount_Alive_30_90_Day",
                "ConnectionCount_Alive_90_more_Day" //
                //
                , "StatementExecuteCount_0_1_Millis", "StatementExecuteCount_1_2_Millis",
                "StatementExecuteCount_2_5_Millis", "StatementExecuteCount_5_10_Millis",
                "StatementExecuteCount_10_20_Millis", "StatementExecuteCount_20_50_Millis",
                "StatementExecuteCount_50_100_Millis", "StatementExecuteCount_100_200_Millis",
                "StatementExecuteCount_200_500_Millis", "StatementExecuteCount_500_1000_Millis",
                "StatementExecuteCount_1_2_Seconds", "StatementExecuteCount_2_5_Seconds",
                "StatementExecuteCount_5_10_Seconds", "StatementExecuteCount_10_30_Seconds",
                "StatementExecuteCount_30_60_Seconds", "StatementExecuteCount_1_2_Minutes",
                "StatementExecuteCount_2_5_Minutes", "StatementExecuteCount_5_10_Minutes",
                "StatementExecuteCount_10_30_Minutes", "StatementExecuteCount_30_more_Minutes"
        //
        };

        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("DataSourceStatistic", "DataSource Statistic", indexNames,
                                           indexDescriptions, indexTypes);

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

        for (JdbcDataSourceStat dataSource : dataSources.values()) {
            ConcurrentMap<String, JdbcSqlStat> statMap = dataSource.getSqlStatMap();
            for (Map.Entry<String, JdbcSqlStat> entry : statMap.entrySet()) {
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
            for (Filter filter : dataSource.getConfig().getFilters()) {
                if (filter instanceof StatFilter) {
                    StatFilter countFilter = (StatFilter) filter;

                    ConcurrentMap<Long, JdbcConnectionStat.Entry> connections = countFilter.getConnections();
                    for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : connections.entrySet()) {
                        data.put(entry.getValue().getCompositeData());
                    }
                }
            }
        }
        
        for (DruidDataSource instance : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            for (Filter filter : instance.getProxyFilters()) {
                if (filter instanceof StatFilter) {
                    StatFilter countFilter = (StatFilter) filter;

                    ConcurrentMap<Long, JdbcConnectionStat.Entry> connections = countFilter.getConnections();
                    for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : connections.entrySet()) {
                        data.put(entry.getValue().getCompositeData());
                    }
                }
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
            for (Filter filter : dataSource.getConfig().getFilters()) {
                if (filter instanceof StatFilter) {
                    StatFilter countFilter = (StatFilter) filter;
                    countFilter.reset();
                }
            }
        }
        
        for (DruidDataSource instance : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            for (Filter filter : instance.getProxyFilters()) {
                if (filter instanceof StatFilter) {
                    StatFilter countFilter = (StatFilter) filter;
                    countFilter.reset();
                }
            }
        }
    }

    @Override
    public long getResetCount() {
        return resetCount.get();
    }
}
