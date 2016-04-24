package com.alibaba.druid.pvt.pool;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class Large10ConcurrentTest extends TestCase {

    private DruidDataSource[]        dataSources;
    private ScheduledExecutorService scheduler;

    private ExecutorService          executor;

    protected void setUp() throws Exception {
        long xmx = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / (1000 * 1000); // m
        
        final int dataSourceCount;

        if (xmx <= 256) {
            dataSourceCount = 1024 * 1;
        } else if (xmx <= 512) {
            dataSourceCount = 1024 * 2;
        } else if (xmx <= 1024) {
            dataSourceCount = 1024 * 4;
        } else if (xmx <= 2048) {
            dataSourceCount = 1024 * 8;
        } else {
            dataSourceCount = 1024 * 16;
        }
        
        dataSources = new DruidDataSource[dataSourceCount];
        
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
        final Connection[] connections = new Connection[dataSources.length * 8];
        final CountDownLatch connLatch = new CountDownLatch(connections.length);
        final AtomicLong connErrorCount = new AtomicLong();

        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final DataSource dataSource = dataSources[i];
                final int index = i * 8 + j;
                Runnable task = new Runnable() {

                    public void run() {
                        try {
                            connections[index] = dataSource.getConnection();
                        } catch (SQLException e) {
                            connErrorCount.incrementAndGet();
                            e.printStackTrace();
                        } finally {
                            connLatch.countDown();
                        }
                    }
                };
                executor.execute(task);
            }
        }
        connLatch.await();
        
        for (int i = 0; i < dataSources.length; ++i) {
            Assert.assertEquals(8, dataSources[i].getActiveCount());
        }
        
        for (int i = 0; i < dataSources.length; ++i) {
            Assert.assertEquals(0, dataSources[i].getPoolingCount());
        }

        final CountDownLatch closeLatch = new CountDownLatch(connections.length);
        for (int i = 0; i < dataSources.length; ++i) {
            for (int j = 0; j < 8; ++j) {
                final int index = i * 8 + j;
                Runnable task = new Runnable() {
                    public void run() {
                        JdbcUtils.close(connections[index]);
                        closeLatch.countDown();
                    }
                };
                executor.execute(task);
            }
        }
        closeLatch.await();
    }
}
