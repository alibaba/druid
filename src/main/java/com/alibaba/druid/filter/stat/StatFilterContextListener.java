package com.alibaba.druid.filter.stat;


public interface StatFilterContextListener {
    void addUpdateCount(int updateCount);
    
    void addFetchRowCount(int fetchRowCount);
}
