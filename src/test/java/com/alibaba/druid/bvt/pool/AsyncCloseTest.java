package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class AsyncCloseTest extends TestCase {

    protected DruidDataSource                     dataSource;
    private ExecutorService                       closeExecutor;

    private ExecutorCompletionService<Connection> completionService;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setAsyncCloseConnectionEnable(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(16);

        closeExecutor = Executors.newFixedThreadPool(100);

        completionService = new ExecutorCompletionService<Connection>(Executors.newFixedThreadPool(100));
    }

    public void test_0() throws Exception {
        for (int i = 0; i < 16; ++i) {
            loop();
        }
    }

    protected void loop() throws InterruptedException {
        dataSource.shrink();
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());
        
        final Callable<Connection> getTask = new Callable<Connection>() {

            @Override
            public Connection call() throws Exception {
                return dataSource.getConnection();
            }

        };

        final int COUNT = 1024 * 1024;
        final CountDownLatch latch = new CountDownLatch(COUNT);

        for (int i = 0; i < COUNT; ++i) {
            completionService.submit(getTask);
        }

        Runnable closeTask = new Runnable() {

            @Override
            public void run() {
                try {
                    Future<Connection> task = completionService.take();
                    Connection conn = task.get();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        };

        for (int i = 0; i < COUNT; ++i) {
            closeExecutor.submit(closeTask);
        }

        latch.await();
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(16, dataSource.getPoolingCount());

    }
}
