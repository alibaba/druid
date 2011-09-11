package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestIdle3_Concurrent extends TestCase {
    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }
    
    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }
    
    public void test_idle2() throws Exception {
        MockDriver driver = new MockDriver();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(30 * 10); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(18 * 10); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        
        // ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource, new ObjectName("com.alibaba:type=DataSource"));

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

        {
            // 并发创建14个
            concurrent(driver, dataSource, 100);
        }

        // 连续打开关闭单个连接
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
        }
        Thread.sleep(1000);
        Assert.assertEquals(1, dataSource.getPoolingCount());

        dataSource.close();
    }

    private void concurrent(final MockDriver driver, final DruidDataSource dataSource, final int count) throws Exception {
        final int LOOP_COUNT = 1000;
        Thread[] threads = new Thread[count];
        final CyclicBarrier barrier = new CyclicBarrier(count);
        final CountDownLatch endLatch = new CountDownLatch(count);
        for (int i = 0; i < count; ++i) {
            threads[i] = new Thread("thread-" + i) {
                public void run() {
                    try {
                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            barrier.await();
                            
                            Connection conn = dataSource.getConnection();
                            {
                                AtomicInteger c = new AtomicInteger();
                                for (int j = 0; j < 1000 * 1; ++j) {
                                    c.incrementAndGet();
                                }
                                c.set(0);
                                Thread.sleep(1);
                            }
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }
        
        for (int i = 0; i < count; ++i) {
            threads[i].start();
        }
        
        endLatch.await();
        System.out.println("concurrent end");
        
        int max = count > dataSource.getMaxActive() ? dataSource.getMaxActive() : count;
        Assert.assertEquals(max, driver.getConnections().size());
        
    }
}
