package com.alibaba.druid.pvt.pool;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.MockExceptionSorter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.Log4jImpl;
import com.alibaba.druid.support.logging.NoLoggingImpl;

import junit.framework.TestCase;

public class AsyncCloseTest3 extends TestCase {

    protected DruidDataSource dataSource;
    private ExecutorService   connExecutor;
    private ExecutorService   closeExecutor;

    final AtomicInteger       errorCount = new AtomicInteger();

    private Logger            log4jLog;
    private Level             log4jOldLevel;

    private NoLoggingImpl     noLoggingImpl;

    long                      xmx;

    protected void setUp() throws Exception {
        xmx = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1000 * 1000); // m;

        System.gc();

        Field logField = DruidDataSource.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        Log dataSourceLog = (Log) logField.get(null);
        if (dataSourceLog instanceof Log4jImpl) {
            this.log4jLog = ((Log4jImpl) dataSourceLog).getLog();
            this.log4jOldLevel = this.log4jLog.getLevel();
            this.log4jLog.setLevel(Level.FATAL);
        } else if (dataSourceLog instanceof NoLoggingImpl) {
            noLoggingImpl =  (NoLoggingImpl) dataSourceLog;
            noLoggingImpl.setErrorEnabled(false);
        }

        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:");
        // dataSource.setAsyncCloseConnectionEnable(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(16);

        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public boolean statement_execute(FilterChain chain, StatementProxy statement,
                                             String sql) throws SQLException {
                throw new SQLException();
            }
        });

        dataSource.setExceptionSorter(new MyExceptionSorter());
        dataSource.init();

        connExecutor = Executors.newFixedThreadPool(2);
        closeExecutor = Executors.newFixedThreadPool(2);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        if (log4jLog != null) {
            log4jLog.setLevel(log4jOldLevel);
        } else if (noLoggingImpl != null) {
            noLoggingImpl.setErrorEnabled(true);
        }
    }

    public void test_0() throws Exception {
        for (int i = 0; i < 16; ++i) {
            loop();
            System.out.println("loop " + i + " done.");
        }
    }

    public static class MyExceptionSorter extends MockExceptionSorter {

        @Override
        public boolean isExceptionFatal(SQLException e) {
            return true;
        }
    }

    class CloseTask implements Runnable {

        private Connection     conn;
        private CountDownLatch latch;

        public CloseTask(Connection conn, CountDownLatch latch){
            this.conn = conn;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                conn.close();
            } catch (SQLException e) {
                errorCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        }

    }

    protected void loop() throws InterruptedException {
        dataSource.shrink();
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());

        final int COUNT;

        if (xmx <= 256) {
            COUNT = 1024 * 8;
        } else if (xmx <= 512) {
            COUNT = 1024 * 16;
        } else if (xmx <= 1024) {
            COUNT = 1024 * 32;
        } else if (xmx <= 2048) {
            COUNT = 1024 * 64;
        } else {
            COUNT = 1024 * 128;
        }

        final CountDownLatch closeLatch = new CountDownLatch(COUNT * 2);
        final CountDownLatch execLatch = new CountDownLatch(COUNT);

        Runnable connTask = new Runnable() {

            @Override
            public void run() {
                try {
                    DruidPooledConnection conn = dataSource.getConnection();
                    Statement stmt = conn.createStatement();

                    CloseTask closeTask = new CloseTask(conn, closeLatch);

                    try {
                        stmt.execute("select 1");
                    } finally {
                        closeExecutor.submit(closeTask);
                        closeExecutor.submit(closeTask); // dup close

                        stmt.close();
                        conn.close();
                    }
                } catch (SQLException e) {
                    errorCount.incrementAndGet();
                } finally {
                    execLatch.countDown();
                }
            }
        };

        for (int i = 0; i < COUNT; ++i) {
            connExecutor.submit(connTask);
        }

        execLatch.await();
        closeLatch.await();
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
}
