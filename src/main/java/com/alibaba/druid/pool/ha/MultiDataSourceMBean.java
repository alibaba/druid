package com.alibaba.druid.pool.ha;


public interface MultiDataSourceMBean {
    void failureDetect();
    
    String[] getDataSourceNames();
    
    boolean restartDataSource(String name);
}
