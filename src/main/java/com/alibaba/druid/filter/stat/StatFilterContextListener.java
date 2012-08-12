package com.alibaba.druid.filter.stat;

public interface StatFilterContextListener {

    void addUpdateCount(int updateCount);

    void addFetchRowCount(int fetchRowCount);

    void executeBefore(String sql, boolean inTransaction);

    void executeAfter(String sql, long nanoSpan, Throwable error);

    void commit();

    void rollback();
}
