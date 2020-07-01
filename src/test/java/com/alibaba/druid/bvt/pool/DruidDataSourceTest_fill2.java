package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest_fill2 extends TestCase {

    private DruidDataSource dataSource;

    private int             maxActive = 100;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(maxActive);
        dataSource.setTestOnBorrow(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_fill() throws Exception {
        final AtomicLong errorCount = new AtomicLong();

        final int THREAD_COUNT = 100;
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    startLatch.countDown();
                    try {
                        Connection conn = dataSource.getConnection();
                        Thread.sleep(2);
                        conn.close();
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        startLatch.await(1000, TimeUnit.MILLISECONDS);

        final CountDownLatch fillLatch = new CountDownLatch(THREAD_COUNT);
        final AtomicLong fillErrorCount = new AtomicLong();
        final AtomicLong fillCount = new AtomicLong();
        Thread fillThread = new Thread() {

            public void run() {
                try {
                    int count = dataSource.fill();
                    fillCount.getAndSet(count);
                } catch (Exception e) {
                    fillErrorCount.incrementAndGet();
                } finally {
                    fillLatch.countDown();
                }
            }
        };
        fillThread.start();
        
        fillLatch.await(1000, TimeUnit.MILLISECONDS);

        Assert.assertEquals(0, fillErrorCount.get());

        for (int i = 0; i < 100; ++i) {
            endLatch.await(100, TimeUnit.MILLISECONDS);
            if (fillCount.get() > 0 || dataSource.isFull()) {
                break;
            }
        }
        Assert.assertTrue("not full", dataSource.isFull());
//        Assert.assertTrue("fillCount zero", fillCount.get() > 0);

    }
}
