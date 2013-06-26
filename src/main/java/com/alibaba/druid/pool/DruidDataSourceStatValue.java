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
package com.alibaba.druid.pool;

import java.util.Date;
import java.util.List;

import com.alibaba.druid.stat.JdbcSqlStatValue;

public class DruidDataSourceStatValue {

    protected String                 name;
    protected String                 dbType;
    protected String                 driverClassName;
    protected String                 url;
    protected String                 userName;
    protected List<String>           filterClassNames;
    protected boolean                removeAbandoned;

    protected int                    initialSize;
    protected int                    minIdle;
    protected int                    maxActive;
    protected int                    queryTimeout;
    protected int                    transactionQueryTimeout;
    protected int                    loginTimeout;
    protected String                 validConnectionCheckerClassName;
    protected String                 exceptionSorterClassName;
    protected boolean                testOnBorrow;
    protected boolean                testOnReturn;
    protected boolean                testWhileIdle;
    protected boolean                defaultAutoCommit;
    protected boolean                defaultReadOnly;
    protected Integer                defaultTransactionIsolation;

    protected int                    activeCount;
    protected int                    activePeak;
    protected long                   activePeakTime;

    protected int                    poolingCount;
    protected int                    poolingPeak;
    protected long                   poolingPeakTime;

    protected long                   connectCount;
    protected long                   closeCount;
    protected long                   waitThreadCount;
    protected long                   notEmptyWaitCount;
    protected long                   notEmptyWaitNanos;

    protected long                   logicConnectErrorCount;
    protected long                   physicalConnectCount;
    protected long                   physicalCloseCount;
    protected long                   physicalConnectErrorCount;
    protected long                   executeCount;
    protected long                   errorCount;
    protected long                   commitCount;
    protected long                   rollbackCount;
    protected long                   pstmtCacheHitCount;
    protected long                   pstmtCacheMissCount;
    protected long                   startTransactionCount;
    protected long[]                 transactionHistogram;
    protected long[]                 connectionHoldTimeHistogram;

    protected long                   clobOpenCount;
    protected long                   blobOpenCount;
    protected long                   sqlSkipCount;

    protected List<JdbcSqlStatValue> sqlList;
    
    public Date getPoolingPeakTime() {
        if (poolingPeakTime <= 0) {
            return null;
        }

        return new Date(poolingPeakTime);
    }

    public long getSqlSkipCount() {
        return sqlSkipCount;
    }

    public void setSqlSkipCount(long sqlSkipCount) {
        this.sqlSkipCount = sqlSkipCount;
    }

    public Date getActivePeakTime() {
        if (activePeakTime <= 0) {
            return null;
        }

        return new Date(activePeakTime);
    }

    public long getNotEmptyWaitMillis() {
        return notEmptyWaitNanos / (1000 * 1000);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getFilterClassNames() {
        return filterClassNames;
    }

    public void setFilterClassNames(List<String> filterClassNames) {
        this.filterClassNames = filterClassNames;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
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

    public int getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public String getValidConnectionCheckerClassName() {
        return validConnectionCheckerClassName;
    }

    public void setValidConnectionCheckerClassName(String validConnectionCheckerClassName) {
        this.validConnectionCheckerClassName = validConnectionCheckerClassName;
    }

    public String getExceptionSorterClassName() {
        return exceptionSorterClassName;
    }

    public void setExceptionSorterClassName(String exceptionSorterClassName) {
        this.exceptionSorterClassName = exceptionSorterClassName;
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

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public boolean isDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public Integer getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(Integer defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getActivePeak() {
        return activePeak;
    }

    public void setActivePeak(int activePeak) {
        this.activePeak = activePeak;
    }

    public int getPoolingCount() {
        return poolingCount;
    }

    public void setPoolingCount(int poolingCount) {
        this.poolingCount = poolingCount;
    }

    public int getPoolingPeak() {
        return poolingPeak;
    }

    public void setPoolingPeak(int poolingPeak) {
        this.poolingPeak = poolingPeak;
    }

    public long getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(long connectCount) {
        this.connectCount = connectCount;
    }

    public long getCloseCount() {
        return closeCount;
    }

    public void setCloseCount(long closeCount) {
        this.closeCount = closeCount;
    }

    public long getWaitThreadCount() {
        return waitThreadCount;
    }

    public void setWaitThreadCount(long waitThreadCount) {
        this.waitThreadCount = waitThreadCount;
    }

    public long getNotEmptyWaitCount() {
        return notEmptyWaitCount;
    }

    public void setNotEmptyWaitCount(long notEmptyWaitCount) {
        this.notEmptyWaitCount = notEmptyWaitCount;
    }

    public long getNotEmptyWaitNanos() {
        return notEmptyWaitNanos;
    }

    public void setNotEmptyWaitNanos(long notEmptyWaitNanos) {
        this.notEmptyWaitNanos = notEmptyWaitNanos;
    }

    public long getLogicConnectErrorCount() {
        return logicConnectErrorCount;
    }

    public void setLogicConnectErrorCount(long logicConnectErrorCount) {
        this.logicConnectErrorCount = logicConnectErrorCount;
    }

    public long getPhysicalConnectCount() {
        return physicalConnectCount;
    }

    public void setPhysicalConnectCount(long physicalConnectCount) {
        this.physicalConnectCount = physicalConnectCount;
    }

    public long getPhysicalCloseCount() {
        return physicalCloseCount;
    }

    public void setPhysicalCloseCount(long physicalCloseCount) {
        this.physicalCloseCount = physicalCloseCount;
    }

    public long getPhysicalConnectErrorCount() {
        return physicalConnectErrorCount;
    }

    public void setPhysicalConnectErrorCount(long physicalConnectErrorCount) {
        this.physicalConnectErrorCount = physicalConnectErrorCount;
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(long executeCount) {
        this.executeCount = executeCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(long commitCount) {
        this.commitCount = commitCount;
    }

    public long getRollbackCount() {
        return rollbackCount;
    }

    public void setRollbackCount(long rollbackCount) {
        this.rollbackCount = rollbackCount;
    }

    public long getPstmtCacheHitCount() {
        return pstmtCacheHitCount;
    }

    public void setPstmtCacheHitCount(long pstmtCacheHitCount) {
        this.pstmtCacheHitCount = pstmtCacheHitCount;
    }

    public long getPstmtCacheMissCount() {
        return pstmtCacheMissCount;
    }

    public void setPstmtCacheMissCount(long pstmtCacheMissCount) {
        this.pstmtCacheMissCount = pstmtCacheMissCount;
    }

    public long getStartTransactionCount() {
        return startTransactionCount;
    }

    public void setStartTransactionCount(long startTransactionCount) {
        this.startTransactionCount = startTransactionCount;
    }

    public long[] getTransactionHistogram() {
        return transactionHistogram;
    }

    public void setTransactionHistogram(long[] transactionHistogram) {
        this.transactionHistogram = transactionHistogram;
    }

    public long[] getConnectionHoldTimeHistogram() {
        return connectionHoldTimeHistogram;
    }

    public void setConnectionHoldTimeHistogram(long[] connectionHoldTimeHistogram) {
        this.connectionHoldTimeHistogram = connectionHoldTimeHistogram;
    }

    public long getClobOpenCount() {
        return clobOpenCount;
    }

    public void setClobOpenCount(long clobOpenCount) {
        this.clobOpenCount = clobOpenCount;
    }

    public long getBlobOpenCount() {
        return blobOpenCount;
    }

    public void setBlobOpenCount(long blobOpenCount) {
        this.blobOpenCount = blobOpenCount;
    }

    public List<JdbcSqlStatValue> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<JdbcSqlStatValue> sqlList) {
        this.sqlList = sqlList;
    }

    public void setActivePeakTime(long activePeakTime) {
        this.activePeakTime = activePeakTime;
    }

    public void setPoolingPeakTime(long poolingPeakTime) {
        this.poolingPeakTime = poolingPeakTime;
    }
    

}
