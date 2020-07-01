package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_getInitStackTrace() {
        String stackTrace = dataSource.getInitStackTrace();
        Assert.assertTrue(stackTrace.indexOf("com.alibaba.druid.bvt.pool.DruidDataSourceTest.setUp") != -1);
    }

    public void test_restart() throws Exception {
        Assert.assertEquals(true, dataSource.isInited());
        {
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(1, dataSource.getActiveCount());

            Exception error = null;
            try {
                dataSource.restart();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
            Assert.assertEquals(true, dataSource.isInited());

            conn.close();
            dataSource.restart();
        }

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(false, dataSource.isInited());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
