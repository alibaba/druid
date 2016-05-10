package com.alibaba.druid.pvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class AsyncCloseTest extends TestCase {

    protected DruidDataSource dataSource;
    private ExecutorService   connExecutor;
    private ExecutorService   closeExecutor;

    final AtomicInteger       errorCount = new AtomicInteger();

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setAsyncCloseConnectionEnable(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(16);

        connExecutor = Executors.newFixedThreadPool(100);
        closeExecutor = Executors.newFixedThreadPool(100);

    }
    
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {
        for (int i = 0; i < 16; ++i) {
            loop();
            System.out.println("loop " + i + " done.");
        }
    }

    class CloseTask implements Runnable {

        private Connection     conn;
        private CountDownLatch latch;

        public CloseTask(Connection conn, CountDownLatch latch){
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
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());

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
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(16, dataSource.getPoolingCount());


    }
}
