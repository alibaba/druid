package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_interrupt2 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setInitialSize(1);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_autoCommit() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        dataSource.getLock().lock();

        try {
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

            assertEquals(0, errorCount.get());

            thread.interrupt();

            endLatch.await();
            assertEquals(1, errorCount.get());
        } finally {
            dataSource.getLock().unlock();
        }
    }
}
