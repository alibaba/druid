package com.alibaba.druid.pool;

public interface DruidC3P0WrapDataSourceMBean {

    String getUser();

    int getCheckoutTimeout();

    boolean isAutoCommitOnClose();

    int getIdleConnectionTestPeriod();

    int getInitialPoolSize();

    int getMaxIdleTime();

    int getMaxPoolSize();

    int getMinPoolSize();

    boolean isTestConnectionOnCheckout();

    boolean isTestConnectionOnCheckin();

    String getPreferredTestQuery();

    // //////////

    boolean isEnable();

    void shrink();

    String toString();

    int getWaitThreadCount();

    int getLockQueueLength();
}
