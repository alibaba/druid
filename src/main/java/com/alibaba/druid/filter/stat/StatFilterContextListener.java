package com.alibaba.druid.filter.stat;

public interface StatFilterContextListener {

    void addUpdateCount(int updateCount);

    void addFetchRowCount(int fetchRowCount);

    void executeBefore(String sql, boolean inTransaction);

    void executeAfter(String sql, long nanoSpan, Throwable error);

    void commit();

    void rollback();
    
    void pool_connect();
    
    void pool_close(long nanos);
    
    void physical_connection_connect();

    void physical_connection_close(long nanos);
    
    void resultSet_open();
    
    void resultSet_close(long nanos);
}
