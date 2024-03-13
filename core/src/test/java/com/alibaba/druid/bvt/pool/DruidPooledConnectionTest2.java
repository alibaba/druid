package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;

public class DruidPooledConnectionTest2 extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setKeepConnectionUnderlyingTransactionIsolation(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        conn.close();

        Exception error = null;
        try {
            conn.handleException(new RuntimeException());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_handleException_1() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        Exception error = null;
        try {
            conn.handleException(new RuntimeException());
        } catch (SQLException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        conn.close();

        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_closePoolableStatement() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        DruidPooledPreparedStatement stmt = (DruidPooledPreparedStatement) conn.prepareStatement("select 1");
        conn.close();
        conn.closePoolableStatement(stmt);
    }

    public void test_dup_close() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();

        conn.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_recycle() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        int defaultIsolation = conn.getTransactionIsolation();
        conn.setTransactionIsolation(defaultIsolation + 1);

        conn.close();
        conn.recycle();

        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());

        conn = (DruidPooledConnection) dataSource.getConnection();
        Assert.assertEquals(defaultIsolation + 1, conn.getTransactionIsolation());
        conn.close();
    }

    public void test_recycle_2() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getConnection();
        int defaultIsolation = conn.getTransactionIsolation();
        conn.setTransactionIsolation(defaultIsolation + 1);

        conn.recycle();
        conn.recycle();
        conn.close();

        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());

        conn = (DruidPooledConnection) dataSource.getConnection();
        Assert.assertEquals(defaultIsolation + 1, conn.getTransactionIsolation());
        conn.close();
    }
}
