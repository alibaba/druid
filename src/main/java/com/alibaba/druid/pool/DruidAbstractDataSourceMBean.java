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

    boolean isTestOnReturn();

    boolean isDefaultAutoCommit();

    Boolean getDefaultReadOnly();

    Integer getDefaultTransactionIsolation();

    String getDefaultCatalog();

    boolean isPoolPreparedStatements();

    long getMaxWait();

    int getMinIdle();

    int getMaxIdle();

    long getCreateErrorCount();

    int getMaxActive();

    long getTimeBetweenConnectErrorMillis();

    int getMaxOpenPreparedStatements();

    long getRemoveAbandonedCount();
    
    boolean isLogAbandoned();
    
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
    
    long getReusePreparedStatementCount();
    
    long getPreparedStatementCount();
}
