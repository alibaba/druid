package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestRemoveAbandoned extends TestCase {

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
        dataSource.setTimeBetweenEvictionRunsMillis(10); // 180 / 10
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_removeAbandoned() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());

        // 超时之后，连接自动关闭
        Thread.sleep(100);
        Assert.assertTrue(conn.isClosed());

        Assert.assertEquals(1, dataSource.getRemoveAbandonedCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}
