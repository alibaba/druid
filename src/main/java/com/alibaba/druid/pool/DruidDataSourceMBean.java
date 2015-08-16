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
package com.alibaba.druid.pool;

import java.sql.SQLException;
import java.util.Date;

import javax.management.ObjectName;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface DruidDataSourceMBean extends DruidAbstractDataSourceMBean {
    long getResetCount();

    boolean isEnable();

    void shrink();

    int removeAbandoned();

    String dump();

    int getWaitThreadCount();

    int getLockQueueLength();

    long getNotEmptyWaitCount();

    int getNotEmptyWaitThreadCount();

    long getNotEmptySignalCount();

    long getNotEmptyWaitMillis();

    long getNotEmptyWaitNanos();

    void resetStat();

    boolean isResetStatEnable();

    void setResetStatEnable(boolean resetStatEnable);

    String getVersion();

    void setPoolPreparedStatements(boolean poolPreparedStatements);

    int getActivePeak();

    int getPoolingPeak();

    Date getActivePeakTime();

    Date getPoolingPeakTime();

    long getErrorCount();

    ObjectName getObjectName();

    void clearStatementCache() throws SQLException;
    
    long getDiscardCount();
    
    void setStatLoggerClassName(String className);
    
    long getTimeBetweenLogStatsMillis();
    
    void setTimeBetweenLogStatsMillis(long timeBetweenLogStatsMillis);
    
    void setConnectionProperties(String connectionProperties);
    
    int fill() throws SQLException;
    
    int fill(int toCount) throws SQLException;
}
