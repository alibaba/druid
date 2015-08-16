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

import java.util.Date;
import java.util.List;

public interface DruidAbstractDataSourceMBean {

    int getLoginTimeout();

    String getDbType();

    String getName();

    int getInitialSize();

    String getUsername();

    String getUrl();

    String getDriverClassName();

    long getConnectCount();

    long getCloseCount();

    long getConnectErrorCount();

    int getPoolingCount();

    long getRecycleCount();

    int getActiveCount();

    long getCreateCount();

    long getDestroyCount();

    long getCreateTimespanMillis();

    long getCommitCount();

    long getRollbackCount();

    long getStartTransactionCount();

    int getQueryTimeout();

    int getTransactionQueryTimeout();

    String getValidationQuery();

    int getValidationQueryTimeout();

    int getMaxWaitThreadCount();

    long getTimeBetweenEvictionRunsMillis();

    long getMinEvictableIdleTimeMillis();

    boolean isRemoveAbandoned();

    long getRemoveAbandonedTimeoutMillis();

    List<String> getActiveConnectionStackTrace();

    List<String> getFilterClassNames();

    boolean isTestOnBorrow();

    void setTestOnBorrow(boolean testOnBorrow);

    boolean isTestOnReturn();

    boolean isTestWhileIdle();

    void setTestWhileIdle(boolean testWhileIdle);

    boolean isDefaultAutoCommit();

    Boolean getDefaultReadOnly();

    Integer getDefaultTransactionIsolation();

    String getDefaultCatalog();

    boolean isPoolPreparedStatements();

    boolean isSharePreparedStatements();

    long getMaxWait();

    int getMinIdle();

    int getMaxIdle();

    long getCreateErrorCount();

    int getMaxActive();
    
    void setMaxActive(int maxActive);

    long getTimeBetweenConnectErrorMillis();

    int getMaxOpenPreparedStatements();

    long getRemoveAbandonedCount();

    boolean isLogAbandoned();

    void setLogAbandoned(boolean logAbandoned);

    long getDupCloseCount();

    boolean isBreakAfterAcquireFailure();

    int getConnectionErrorRetryAttempts();

    int getMaxPoolPreparedStatementPerConnectionSize();

    void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize);

    String getProperties();

    int getRawDriverMinorVersion();

    int getRawDriverMajorVersion();

    Date getCreatedTime();

    String getValidConnectionCheckerClassName();

    long[] getTransactionHistogramValues();

    void setTransactionThresholdMillis(long transactionThresholdMillis);

    long getTransactionThresholdMillis();

    long getPreparedStatementCount();

    long getClosedPreparedStatementCount();

    long getCachedPreparedStatementCount();

    long getCachedPreparedStatementDeleteCount();

    long getCachedPreparedStatementAccessCount();

    long getCachedPreparedStatementMissCount();

    long getCachedPreparedStatementHitCount();
    
    boolean isUseOracleImplicitCache();
    
    void setUseOracleImplicitCache(boolean useOracleImplicitCache);
    
    int getDriverMajorVersion();
    
    int getDriverMinorVersion();
    
    String getExceptionSorterClassName();
}
