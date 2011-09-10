package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DataSourceDisableException;
import com.alibaba.druid.pool.DruidDataSource;


public class TestGraceShutdown extends TestCase {
    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
    }
    
    public void test_close() throws Exception {
        final int threadCount = 100;
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        Connection conn = dataSource.getConnection();
                    } catch (DataSourceDisableException ex) {
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
        Thread.sleep(1000);
        
        new Thread("close thread") {
            public void run() {
                dataSource.close();
            }
        }.start();

        endLatch.await();
    }
}
