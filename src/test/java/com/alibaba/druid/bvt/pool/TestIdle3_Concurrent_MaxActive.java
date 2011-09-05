package com.alibaba.druid.bvt.pool;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.management.ObjectName;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdle3_Concurrent_MaxActive extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
    }

    public void test_idle2() throws Exception {

        ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
                                                                 new ObjectName("com.alibaba:type=DataSource"));

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

        for (int i = 0; i < 1000; ++i) {
            concurrent(200);
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getPoolingCount());
            dataSource.shrink();
            Assert.assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());
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

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        Connection conn = dataSource.getConnection();
                        long millis = new Random().nextInt(5) + 10;
                        Thread.sleep(millis);
                        conn.close();
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
