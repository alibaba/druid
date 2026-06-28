package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_notEmptyWait2 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(1);

        dataSource.setMaxWaitThreadCount(10);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_maxWaitThread() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Connection conn = dataSource.getConnection();
        final AtomicLong errorCount = new AtomicLong();

        final int THREAD_COUNT = 10;
        final CountDownLatch startLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread("thread-" + i) {
                public void run() {
                    startLatch.countDown();
                    try {
                        System.out.println(Thread.currentThread() + " " + LocalDateTime.now() + " begin ");
                        Connection conn = dataSource.getConnection();
                        Thread.sleep(2);
                        System.out.println(Thread.currentThread() + " " + LocalDateTime.now() + " getConnection== " + conn);
                        conn.close();
                    } catch (Exception e) {
                         e.printStackTrace();
                        errorCount.incrementAndGet();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        startLatch.await(100, TimeUnit.MILLISECONDS);

        // 等待所有10个线程都进入等待状态（notEmptyWaitThreadCount == 10）
        for (int i = 0; i < 100; i++) {
            if (dataSource.getNotEmptyWaitThreadCount() == THREAD_COUNT) {
                break;
            }
            Thread.sleep(10);
        }
        System.out.println("notEmptyWaitThreadCount = " + dataSource.getNotEmptyWaitThreadCount());

        final CountDownLatch errorThreadEndLatch = new CountDownLatch(THREAD_COUNT);
        final AtomicLong maxWaitErrorCount = new AtomicLong();
        Thread errorThread = new Thread() {
            public void run() {
                try {
                    Connection conn = dataSource.getConnection();
                    Thread.sleep(1);
                    conn.close();
                } catch (Exception e) {
                    System.out.println("errorThread caught exception: " + e.getMessage());
                    maxWaitErrorCount.incrementAndGet();
                } finally {
                    errorThreadEndLatch.countDown();
                }
            }
        };
        errorThread.start();

        errorThreadEndLatch.await(100, TimeUnit.MILLISECONDS);

        // 修改后使用 >=，当 notEmptyWaitThreadCount >= maxWaitThreadCount 时抛出异常
        // maxWaitThreadCount=10，前10个线程进入等待后计数为10，第11个线程应该抛出异常
        assertEquals(1, maxWaitErrorCount.get());
        assertTrue(dataSource.getNotEmptySignalCount() > 0);

        conn.close();

        System.out.println(Thread.currentThread() + " " + LocalDateTime.now() + "释放了连接");
        endLatch.await(100, TimeUnit.MILLISECONDS);
        assertEquals(0, errorCount.get());
    }
}
