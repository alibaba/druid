package com.alibaba.druid;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class DBCPTest extends TestCase {

    public void test_max() throws Exception {
        Class.forName("com.alibaba.druid.mock.MockDriver");
        
        final BasicDataSource dataSource = new BasicDataSource();
//        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(3);
        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(20);
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:mock:xxx");

        final int THREAD_COUNT = 200;
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch startLatch = new CountDownLatch(1);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread() {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < 1000; ++i) {
                            Connection conn = dataSource.getConnection();
                            Thread.sleep(1);
                            conn.close();
                        }
                    } catch (Exception e) {
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }
        
        startLatch.countDown();
        
        endLatch.await();
        
//        System.out.println(dataSource.getNumIdle());
        System.out.println(MockDriver.instance.getConnections().size());
        System.out.println(MockDriver.instance.getConnectionCloseCount());
    }
}
