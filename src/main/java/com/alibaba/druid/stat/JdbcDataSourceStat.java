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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.JMException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

public class JdbcDataSourceStat implements JdbcDataSourceStatMBean {

    private final String                                        name;
    private final String                                        url;

    private final JdbcConnectionStat                            connectionStat = new JdbcConnectionStat();
    private final JdbcResultSetStat                             resultSetStat  = new JdbcResultSetStat();
    private final JdbcStatementStat                             statementStat  = new JdbcStatementStat();

    private final ConcurrentMap<String, JdbcSqlStat>            sqlStatMap     = new ConcurrentHashMap<String, JdbcSqlStat>();

    private final ConcurrentMap<Long, JdbcConnectionStat.Entry> connections    = new ConcurrentHashMap<Long, JdbcConnectionStat.Entry>();

    public JdbcDataSourceStat(String name, String url){
        this.name = name;
        this.url = url;
    }

    public void reset() {
        connectionStat.reset();
        statementStat.reset();
        resultSetStat.reset();

        sqlStatMap.clear();

        for (JdbcConnectionStat.Entry connectionStat : connections.values()) {
            connectionStat.reset();
        }
        // connections.clear();
    }

    public JdbcConnectionStat getConnectionStat() {
        return connectionStat;
    }

    public JdbcResultSetStat getResultSetStat() {
        return resultSetStat;
    }

    public JdbcStatementStat getStatementStat() {
        return statementStat;
    }

    @Override
    public String getConnectionUrl() {
        return url;
    }

    public JdbcSqlStat getSqlStat(String sql) {
        return this.sqlStatMap.get(sql);
    }

    @Override
    public TabularData getSqlList() throws JMException {
        CompositeType rowType = JdbcSqlStat.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("SqlListStatistic", "SqlListStatistic", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        for (Map.Entry<String, JdbcSqlStat> entry : sqlStatMap.entrySet()) {
            data.put(entry.getValue().getCompositeData());
        }

        return data;
    }

    public static StatFilter getStatFilter(DataSourceProxy dataSource) {
        for (Filter filter : dataSource.getFilters()) {
            if (filter instanceof StatFilter) {
                return (StatFilter) filter;
            }
        }

        return null;
    }

    public JdbcSqlStat getSqlStat(long id) {
        for (Map.Entry<String, JdbcSqlStat> entry : this.sqlStatMap.entrySet()) {
            if (entry.getValue().getId() == id) {
                return entry.getValue();
            }
        }

        return null;
    }

    public final ConcurrentMap<Long, JdbcConnectionStat.Entry> getConnections() {
        return connections;
    }

    @Override
    public TabularData getConnectionList() throws JMException {
        CompositeType rowType = JdbcConnectionStat.Entry.getCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("ConnectionListStatistic", "ConnectionListStatistic", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        for (Map.Entry<Long, JdbcConnectionStat.Entry> entry : getConnections().entrySet()) {
            data.put(entry.getValue().getCompositeData());
        }

        return data;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public ConcurrentMap<String, JdbcSqlStat> getSqlStatMap() {
        return sqlStatMap;
    }

    @Override
    public long getConnectionActiveCount() {
        return this.connections.size();
    }

    @Override
    public long getConnectionConnectAliveMillis() {
        long nowNano = System.nanoTime();
        long aliveNanoSpan = this.getConnectionStat().getAliveTotal();

        for (JdbcConnectionStat.Entry connection : connections.values()) {
            aliveNanoSpan += nowNano - connection.getEstablishNano();
        }
        return aliveNanoSpan / (1000 * 1000);
    }

    public long getConnectionConnectAliveMillisMax() {
        long max = this.getConnectionStat().getAliveNanoMax();

        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano > max) {
                max = connectionAliveNano;
            }
        }
        return max / (1000 * 1000);
    }

    public long getConnectionConnectAliveMillisMin() {
        long min = this.getConnectionStat().getAliveNanoMin();

        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano < min || min == 0) {
                min = connectionAliveNano;
            }
        }
        return min / (1000 * 1000);
    }

    final static long SECOND = 1000 * 1000 * 1000;
    final static long MINUTE = SECOND * 60;
    final static long HOUR   = MINUTE * 60;
    final static long DAY    = HOUR * 24;

    public long getConnectionCount_Alive_0_1_Seconds() {
        long count = this.getConnectionStat().getCount_Alive_0_1_Seconds();

        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano < SECOND) {
                count++;
            }
        }

        return count;
    }
    
    public long getConnectionCount_Alive_1_5_Seconds() {
        long count = this.getConnectionStat().getCount_Alive_1_5_Seconds();

        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= SECOND && connectionAliveNano < 5 * SECOND) {
                count++;
            }
        }

        return count;
    }
    
    public long getConnectionCount_Alive_5_10_Seconds() {
        long count = this.getConnectionStat().getCount_Alive_5_10_Seconds();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 5 * SECOND && connectionAliveNano < 10 * SECOND) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_10_30_Seconds() {
        long count = this.getConnectionStat().getCount_Alive_10_30_Seconds();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 10 * SECOND && connectionAliveNano < 30 * SECOND) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_30_60_Seconds() {
        long count = this.getConnectionStat().getCount_Alive_30_60_Seconds();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 30 * SECOND && connectionAliveNano < 60 * SECOND) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_1_5_Minutes() {
        long count = this.getConnectionStat().getCount_Alive_1_5_Minutes();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 1 * MINUTE && connectionAliveNano < 5 * MINUTE) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_5_10_Minutes() {
        long count = this.getConnectionStat().getCount_Alive_5_10_Minutes();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 5 * MINUTE && connectionAliveNano < 10 * MINUTE) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_10_30_Minutes() {
        long count = this.getConnectionStat().getCount_Alive_10_30_Minutes();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 10 * MINUTE && connectionAliveNano < 30 * MINUTE) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_30_60_Minutes() {
        long count = this.getConnectionStat().getCount_Alive_30_60_Minutes();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 30 * MINUTE && connectionAliveNano < 60 * MINUTE) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_1_3_Hours() {
        long count = this.getConnectionStat().getCount_Alive_1_3_Hours();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 1 * HOUR && connectionAliveNano < 3 * HOUR) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_3_6_Hours() {
        long count = this.getConnectionStat().getCount_Alive_3_6_Hours();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 3 * HOUR && connectionAliveNano < 6 * HOUR) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_6_12_Hours() {
        long count = this.getConnectionStat().getCount_Alive_6_12_Hours();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 6 * HOUR && connectionAliveNano < 12 * HOUR) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_12_24_Hours() {
        long count = this.getConnectionStat().getCount_Alive_12_24_Hours();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 12 * HOUR && connectionAliveNano < 24 * HOUR) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_1_3_Day() {
        long count = this.getConnectionStat().getCount_Alive_1_3_Day();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 1 * DAY && connectionAliveNano < 2 * DAY) {
                count++;
            }
        }
        
        return count;
    }
    
    public long getConnectionCount_Alive_3_more_Day() {
        long count = this.getConnectionStat().getCount_Alive_3_more_Day();
        
        long nowNano = System.nanoTime();
        for (JdbcConnectionStat.Entry connection : connections.values()) {
            long connectionAliveNano = nowNano - connection.getEstablishNano();
            if (connectionAliveNano >= 2 * DAY) {
                count++;
            }
        }
        
        return count;
    }
    
}
