package com.alibaba.druid.pool.ha;


public interface MultiDataSourceMBean {
    void failureDetect();
    
    String[] getDataSourceNames();
    
    boolean restartDataSource(String name);
    
    boolean startFailureDetectScheduleTask();
    
    boolean stopFailureDetectScheduleTask();
    
    boolean startConfigLoadScheduleTask();
    
    boolean stopConfigLoadScheduleTask();
    
    long getConfigLoadCount();
    
    long getFailureDetectCount();
    
    long getRetryGetConnectionCount();
    
    long getBusySkipCount();
    
    int getTotalWeight();
    
    long getFailureDetectPeriodMillis();
    
    long getConfigLoadPeriodMillis();
    
    long getActiveCount();
    
    int getMaxPoolSize();
    
    int produceRandomNumber();
}
