package com.alibaba.druid.pool.ha.selector;

import javax.sql.DataSource;

/**
 * A class holding DataSource reference and retrieving time.
 *
 * @author DigitalSonic
 */
public class StickyDataSourceHolder {
    private long retrievingTime = System.currentTimeMillis();
    private DataSource dataSource;

    public StickyDataSourceHolder() {
    }

    public StickyDataSourceHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isValid() {
        return retrievingTime > 0 && dataSource != null;
    }

    public long getRetrievingTime() {
        return retrievingTime;
    }

    public void setRetrievingTime(long retrievingTime) {
        this.retrievingTime = retrievingTime;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
