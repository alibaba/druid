package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest_interrupt3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setMaxActive(1);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_autoCommit() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Connection conn = dataSource.getConnection();

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        final AtomicInteger errorCount = new AtomicInteger();
        Thread thread = new Thread() {

            public void run() {
                try {
                    startLatch.countDown();
                    dataSource.getConnection();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        startLatch.await();

        Thread.sleep(10);

        Assert.assertEquals(0, errorCount.get());

        thread.interrupt();

        endLatch.await();
        Assert.assertEquals(1, errorCount.get());

        conn.close();
    }
}
