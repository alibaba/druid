package com.alibaba.druid.pool;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.ObjectName;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdle3_Concurrent_Starvation_Longtime extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(100);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        
        ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
                                                                 new ObjectName("com.alibaba:type=DataSource"));
    }
    
    protected void tearDown() throws Exception {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName("com.alibaba:type=DataSource"));
    }

    public void test_idle2() throws Exception {

        // 第一次创建连接
        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(1, driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        for (int i = 0; i < 1; ++i) {
            final int threadCount = 100;
            concurrent(threadCount);
        }

        // 连续打开关闭单个连接
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
        }
        // Assert.assertEquals(2, dataSource.getPoolingCount());

        dataSource.close();
    }

    private void concurrent(final int threadCount) throws Exception {
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        final AtomicInteger pass = new AtomicInteger();

        final CyclicBarrier closedBarrier = new CyclicBarrier(threadCount, new Runnable() {
            public void run() {
                Assert.assertEquals(threadCount, dataSource.getPoolingCount());
                dataSource.shrink(false);
                Assert.assertEquals(0, dataSource.getActiveCount());
                Assert.assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());
                if (pass.getAndIncrement() % 100 == 0) {
                    System.out.println("pass : " + pass.get());
                }
            }
        });
        final CyclicBarrier closeBarrier = new CyclicBarrier(threadCount, new Runnable() {
            public void run() {
                Assert.assertEquals(threadCount, dataSource.getActiveCount());
            }
        });

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < 1000 * 1; ++i) {
                            
                            Connection conn = dataSource.getConnection();
                            closeBarrier.await();
                            
                            conn.close();
                            closedBarrier.await();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        startLatch.countDown();
        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }

        endLatch.await();

        // int max = count > dataSource.getMaxActive() ? dataSource.getMaxActive() : count;
        // Assert.assertEquals(max, driver.getConnections().size());
    }
}
