package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;

public class CreateSchedulerTest extends PoolTestCase {
    private DruidDataSource dataSource;
    private ScheduledExecutorService createScheduler;

    protected void setUp() throws Exception {
        super.setUp();

        createScheduler = Executors.newScheduledThreadPool(1);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDestroyScheduler(createScheduler);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        createScheduler.shutdown();

        super.tearDown();
    }

    public void test_connectAndClose() throws Exception {
        Connection[] connections = new Connection[8];
        for (int i = 0; i < connections.length; ++i) {
            connections[i] = dataSource.getConnection();
        }

        for (int i = 0; i < connections.length; ++i) {
            connections[i].close();
        }

        Thread.sleep(100);
        assertEquals(0, dataSource.getPoolingCount());
    }
}
