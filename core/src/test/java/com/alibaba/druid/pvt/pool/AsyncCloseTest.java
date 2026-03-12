package com.alibaba.druid.pvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AsyncCloseTest {
    protected DruidDataSource dataSource;
    private ExecutorService connExecutor;
    private ExecutorService closeExecutor;

    final AtomicInteger errorCount = new AtomicInteger();

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setAsyncCloseConnectionEnable(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(16);

        connExecutor = Executors.newFixedThreadPool(100);
        closeExecutor = Executors.newFixedThreadPool(100);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_0() throws Exception {
        for (int i = 0; i < 16; ++i) {
            loop();
            System.out.println("loop " + i + " done.");
        }
    }

    class CloseTask implements Runnable {
        private Connection conn;
        private CountDownLatch latch;

        public CloseTask(Connection conn, CountDownLatch latch) {
            this.conn = conn;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                conn.close();
            } catch (SQLException e) {
                errorCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        }

    }

    protected void loop() throws InterruptedException {
        dataSource.shrink();
        assertEquals(0, dataSource.getActiveCount());
        assertEquals(0, dataSource.getPoolingCount());

        final int COUNT = 1024 * 128;
        final CountDownLatch closeLatch = new CountDownLatch(COUNT * 2);

        Runnable connTask = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection conn = dataSource.getConnection();

                    CloseTask closeTask = new CloseTask(conn, closeLatch);

                    closeExecutor.submit(closeTask);
                    closeExecutor.submit(closeTask); // dup close
                } catch (SQLException e) {
                    errorCount.incrementAndGet();
                }
            }
        };

        for (int i = 0; i < COUNT; ++i) {
            connExecutor.submit(connTask);
        }

        closeLatch.await();
        assertEquals(0, dataSource.getActiveCount());
        assertEquals(16, dataSource.getPoolingCount());
    }
}
