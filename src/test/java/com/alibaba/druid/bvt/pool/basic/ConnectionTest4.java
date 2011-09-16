package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionTest4 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat,trace");
        dataSource.setPoolPreparedStatements(true);

        JdbcStatContext context = new JdbcStatContext();
        context.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(context);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_basic() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        Assert.assertEquals(null, conn.unwrap(Date.class));
        Assert.assertEquals(conn, conn.unwrap(Connection.class));

        Assert.assertEquals(true, conn.isWrapperFor(PoolableConnection.class));
        Assert.assertEquals(true, conn.isWrapperFor(Connection.class));

        conn.close();
    }

    public void test_prepareStatement_error() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error3() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();

        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                      ResultSet.HOLD_CURSORS_OVER_COMMIT);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }
}
