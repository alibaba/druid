package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Large10KConcurrentTest_2 extends TestCase {

    private DruidDataSource[]        dataSources = new DruidDataSource[10000];
    private ScheduledExecutorService scheduler;

    private ExecutorService          executor;

    protected void setUp() throws Exception {
        executor = Executors.newFixedThreadPool(100);
        scheduler = Executors.newScheduledThreadPool(10);
        for (int i = 0; i < dataSources.length; ++i) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setMaxActive(8);
            dataSource.setMinIdle(0);
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setCreateScheduler(scheduler);
            dataSource.setDestroyScheduler(scheduler);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(false);
            dataSource.setAsyncCloseConnectionEnable(true);

            dataSources[i] = dataSource;
        }
    }

    protected void tearDown() throws Exception {
        for (int i = 0; i < dataSources.length; ++i) {
            JdbcUtils.close(dataSources[i]);
        }
        executor.shutdown();
        scheduler.shutdown();
    }

    public void test_large() throws Exception {
        final Connection[] connections_A = new Connection[dataSources.length * 8];
        final Connection[] connections_B = new Connection[dataSources.length * 8];
        final CountDownLatch connLatch_A = new CountDownLatch(connections_A.length);
        final CountDownLatch connLatch_B = new CountDownLatch(connections_A.length);
        final CountDownLatch closeLatch_A = new CountDownLatch(connections_A.length);
        final CountDownLatch closeLatch_B = new CountDownLatch(connections_A.length);
        final AtomicLong connErrorCount = new AtomicLong();

        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final DataSource dataSource = dataSources[i];
                final int index = i * 8 + j;
                Runnable task = new Runnable() {

                    public void run() {
                        try {
                            connections_A[index] = dataSource.getConnection();
                        } catch (SQLException e) {
                            connErrorCount.incrementAndGet();
                            e.printStackTrace();
                        } finally {
                            connLatch_A.countDown();
                        }
                    }
                };
                executor.execute(task);
            }
        }
        connLatch_A.await();
        
        for (int i = 0; i < dataSources.length; ++i) {
            Assert.assertEquals(8, dataSources[i].getActiveCount());
        }
        
        for (int i = 0; i < dataSources.length; ++i) {
            Assert.assertEquals(0, dataSources[i].getPoolingCount());
        }

        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int index = i * 8 + j;
                Runnable task = new Runnable() {
                    public void run() {
                        JdbcUtils.close(connections_A[index]);
                        closeLatch_A.countDown();
                    }
                };
                executor.execute(task);
            }
        }
        
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final DataSource dataSource = dataSources[i];
                final int index = i * 8 + j;
                Runnable task = new Runnable() {

                    public void run() {
                        try {
                            connections_B[index] = dataSource.getConnection();
                        } catch (SQLException e) {
                            connErrorCount.incrementAndGet();
                            e.printStackTrace();
                        } finally {
                            connLatch_B.countDown();
                        }
                    }
                };
                executor.execute(task);
            }
        }
        closeLatch_A.await();
        connLatch_B.await();
        
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int index = i * 8 + j;
                Runnable task = new Runnable() {
                    public void run() {
                        JdbcUtils.close(connections_B[index]);
                        closeLatch_B.countDown();
                    }
                };
                executor.execute(task);
            }
        }
        closeLatch_B.await();
    }
}
