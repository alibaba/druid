package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;

public class ParamTest extends TestCase {

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_default() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        for (int i = 0; i < 2; ++i) {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
            Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());

            conn.close();

            Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
            Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount()); // logic
                                                                                                       // close不会导致计数器＋1
        }

        dataSource.close();

        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());
    }

    public void test_zero() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setInitialSize(0);
        dataSource.setMaxActive(0);
        dataSource.setMinIdle(0);
        dataSource.setMaxIdle(0);

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        Exception error = null;
        try {
            dataSource.getConnection();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);

        dataSource.close();
    }

    public void test_1() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(0);
        dataSource.setMaxIdle(10);

        dataSource.setFilters("stat");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());

        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Assert.assertEquals(10, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());

        dataSource.close();
    }
}
