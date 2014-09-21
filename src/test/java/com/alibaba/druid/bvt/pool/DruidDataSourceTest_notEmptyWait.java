package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试maxActive < 0
 * 
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSourceTest_notEmptyWait extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setMaxActive(1);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
            Assert.assertEquals(1, dataSource.getNotEmptyWaitCount());
        }

        {
            Connection conn = dataSource.getConnection();
            conn.close();
            Assert.assertEquals(1, dataSource.getNotEmptyWaitCount()); // notEmptyWaitCount没有增长
        }

        Connection conn = dataSource.getConnection();

        final int THREAD_COUNT = 10;
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    startLatch.countDown();
                    try {
                        Connection conn = dataSource.getConnection();
                        Thread.sleep(1);
                        conn.close();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        startLatch.await(10, TimeUnit.MILLISECONDS);

        for (int i = 0; i < 100; ++i) {
            if (dataSource.getNotEmptyWaitThreadCount() == 10) {
                break;
            }
            Thread.sleep(10);
        }
        
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadCount());
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadPeak());

        conn.close();

        endLatch.await(100, TimeUnit.MILLISECONDS);
        
        Thread.sleep(10);
//        Assert.assertEquals(0, dataSource.getNotEmptyWaitThreadCount());
        Assert.assertEquals(10, dataSource.getNotEmptyWaitThreadPeak());
    }
}
