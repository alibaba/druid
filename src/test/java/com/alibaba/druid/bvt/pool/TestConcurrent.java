package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestConcurrent extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        driver = new MockDriver();
        driver.setLogExecuteQueryEnable(false);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(100);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(1 * 100);
        dataSource.setTimeBetweenEvictionRunsMillis(1 * 10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(false, dataSource.isEnable());
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        // 第一次建立连接
        {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(false, dataSource.isEnable());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());

            conn.close();

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
        }

        Assert.assertEquals(true, dataSource.isEnable());

        // 连续打开关闭单个连接
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());
        }

        // 使用单个线程模拟并发打开10个连接
        for (int i = 0; i < 1000 * 1; ++i) {
            final int COUNT = 10;
            Connection[] connections = new Connection[COUNT];

            for (int j = 0; j < connections.length; ++j) {
                connections[j] = dataSource.getConnection();

                Assert.assertEquals(j + 1, dataSource.getActiveCount());
            }

            Assert.assertEquals(COUNT, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());

            for (int j = 0; j < connections.length; ++j) {
                connections[j].close();
                Assert.assertEquals(j + 1, dataSource.getPoolingCount());
            }

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getPoolingCount());
        }

        // 2个并发
        for (int i = 0; i < 3; ++i) {
            concurrent(2);
        }

        // 5个并发
        for (int i = 0; i < 3; ++i) {
            concurrent(5);
        }

        // 10并发
        for (int i = 0; i < 3; ++i) {
            concurrent(10);
        }

        // 20并发
        for (int i = 0; i < 3; ++i) {
            concurrent(20);
        }

        // 50并发
        for (int i = 0; i < 3; ++i) {
            concurrent(50);
        }

        // 100并发
        for (int i = 0; i < 3; ++i) {
            concurrent(100);
        }
    }

    /**
     * 并发执行10000次
     * 
     * @param threadCount
     * @throws InterruptedException
     */
    private void concurrent(final int threadCount) throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < 1000 * 1; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT 1");
                            rs.close();
                            stmt.close();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        dataSource.shrink();
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());

        System.out.println(threadCount + "-threads start");
        startLatch.countDown();
        endLatch.await();
        System.out.println(threadCount + "-threads complete");

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertTrue(threadCount >= dataSource.getPoolingCount());
    }
}
