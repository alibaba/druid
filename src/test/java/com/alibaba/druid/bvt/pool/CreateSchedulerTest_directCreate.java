package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CreateSchedulerTest_directCreate extends PoolTestCase {

    private DruidDataSource[]        dataSources;
    private ScheduledExecutorService createScheduler;
    private ScheduledExecutorService destroyScheduler;

    protected void setUp() throws Exception {
        super.setUp();

        createScheduler = Executors.newScheduledThreadPool(1);
        destroyScheduler = Executors.newScheduledThreadPool(1);

        dataSources = new DruidDataSource[8];
        for (int i = 0; i < dataSources.length; ++i) {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl("jdbc:mock:xxx");
            dataSource.setDriver(new SlowDriver());
            dataSource.setCreateScheduler(createScheduler);
            dataSource.setDestroyScheduler(destroyScheduler);
            dataSource.setMinEvictableIdleTimeMillis(10);
            dataSource.setTimeBetweenEvictionRunsMillis(10);
            dataSource.setFilters("log4j");
            dataSource.setName("ds_" + i);

            dataSources[i] = dataSource;
        }
    }

    protected void tearDown() throws Exception {
        for (int i = 0; i < dataSources.length; ++i) {
            JdbcUtils.close(dataSources[i]);
        }

        createScheduler.shutdown();

        super.tearDown();
    }

    public void test_connectAndClose() throws Exception {
        Thread[] threads = new Thread[32];
        final CountDownLatch latch = new CountDownLatch(threads.length);
        for (int i = 0; i < threads.length; ++i) {
            final int threadId = i;
            int dataSourceIndex = i % dataSources.length;
            final DruidDataSource dataSource = dataSources[dataSourceIndex];
            Thread thread = new Thread(new Task(dataSource, latch), "thread-" + threadId);
            threads[i] = thread;
        }

        for (int i = 0; i < threads.length; ++i) {
            threads[i].start();
            Thread.sleep(10);
        }

        latch.await();
    }

    public static class SlowDriver extends MockDriver {
        public MockConnection createMockConnection(MockDriver driver, String url, Properties connectProperties) {
            try {
                Thread.sleep(1000 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return super.createMockConnection(driver, url, connectProperties);
        }
    }

    public static class Task implements Runnable {
        private final DruidDataSource dataSource;
        private final CountDownLatch latch;

        public Task(DruidDataSource dataSource, CountDownLatch latch) {
            this.dataSource = dataSource;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 4; ++i) {
                    Connection conn = null;
                    try {
                        conn = dataSource.getConnection();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        JdbcUtils.close(conn);
                    }
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
