package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.valid.DefaultValidDataSourceChecker;
import com.alibaba.druid.pool.ha.valid.ValidDataSourceChecker;

public abstract class MultiDataSource extends DataSourceAdapter {

    private Properties               properties                       = new Properties();

    private final AtomicInteger      connectionIdSeed                 = new AtomicInteger();
    private final AtomicInteger      statementIdSeed                  = new AtomicInteger();

    private ValidDataSourceChecker   validDataSourceChecker           = new DefaultValidDataSourceChecker();
    private long                     validDataSourceCheckPeriodMillis = 3000;

    private int                      schedulerThreadCount             = 3;
    private ScheduledExecutorService scheduler;

    private boolean                  inited                           = false;
    private final Lock               lock                             = new ReentrantLock();

    public MultiDataSource(){

    }

    public void init() {
        if (inited) {
            return;
        }

        lock.lock();
        try {
            if (inited) {
                return;
            }

            scheduler = Executors.newScheduledThreadPool(schedulerThreadCount);
            scheduler.scheduleAtFixedRate(new ValidTask(), validDataSourceCheckPeriodMillis,
                                          validDataSourceCheckPeriodMillis, TimeUnit.MILLISECONDS);
            inited = true;
        } finally {
            lock.unlock();
        }
    }

    public long getValidDataSourceCheckPeriodMillis() {
        return validDataSourceCheckPeriodMillis;
    }

    public void setValidDataSourceCheckPeriodMillis(long validDataSourceCheckPeriodMillis) {
        this.validDataSourceCheckPeriodMillis = validDataSourceCheckPeriodMillis;
    }

    public ValidDataSourceChecker getValidDataSourceChecker() {
        return validDataSourceChecker;
    }

    public void setValidDataSourceChecker(ValidDataSourceChecker validDataSourceChecker) {
        this.validDataSourceChecker = validDataSourceChecker;
    }

    public int createConnectionId() {
        return connectionIdSeed.getAndIncrement();
    }

    public int createStatementId() {
        return statementIdSeed.incrementAndGet();
    }

    public abstract List<DruidDataSource> getDataSources();

    public Properties getProperties() {
        return properties;
    }

    public Connection getConnection() throws SQLException {
        init();

        return new MultiDataSourceConnection(this, createConnectionId());
    }

    public abstract Connection getConnectionInternal(MultiDataSourceConnection conn, String sql) throws SQLException;

    public abstract void handleNotAwailableDatasource(DruidDataSource dataSource);

    class ValidTask implements Runnable {

        @Override
        public void run() {
            for (DruidDataSource dataSource : getDataSources()) {
                boolean isValid = validDataSourceChecker.isValid(dataSource);
                if (isValid) {
                    handleNotAwailableDatasource(dataSource);
                }
            }
        }

    }
}
