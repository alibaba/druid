package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import com.alibaba.druid.PoolTestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DestorySchedulerTest extends PoolTestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
    }

    protected void tearDown() throws Exception {
        dataSource.close();

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

        // the shrink interval is at least 1000ms.
        Thread.sleep(1000 * 3);
        Assert.assertEquals(0, dataSource.getPoolingCount());
    }
}
