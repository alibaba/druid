package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestAbondon extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        dataSource = new DruidDataSource();
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(10);
        dataSource.setLogAbandoned(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000);
        dataSource.setUrl("jdbc:mock:xxx");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(false, conn.isClosed());
        Thread.sleep(200);
        Assert.assertEquals(true, conn.isClosed());
    }
}
