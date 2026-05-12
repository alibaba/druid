package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_interrupt {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource() {
            protected void createAndStartCreatorThread() {
                return;
            }
        };

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
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(1);
        final AtomicInteger errorCount = new AtomicInteger();
        Thread thread = new Thread() {
            public void run() {
                try {
                    startLatch.countDown();
                    // not wait createConnectionThread latch as it is null: createAndStartCreatorThread method is mocked.
                    dataSource.init();
                    Thread.sleep(1000);
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
    }
}
