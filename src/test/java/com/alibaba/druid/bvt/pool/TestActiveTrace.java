package com.alibaba.druid.bvt.pool;

import java.lang.management.ManagementFactory;
import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestActiveTrace extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.cear();

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

    public void test_activeTrace() throws Exception {
        for (int i = 0; i < 1000 * 1000; ++i) {
            dataSource.shrink();

            Connection conn = dataSource.getConnection();
            conn.close();
            // Assert.assertEquals(1, dataSource.getPoolingCount());
            dataSource.shrink();
            Assert.assertEquals("createCount : " + dataSource.getCreateCount(), 0, dataSource.getPoolingCount());
            Assert.assertEquals(0, dataSource.getActiveConnections().size());
        }
    }
}
