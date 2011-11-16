package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
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
        Assert.assertEquals(null, conn.unwrap(null));
        Assert.assertEquals(conn, conn.unwrap(Connection.class));

        Assert.assertEquals(false, conn.isWrapperFor(null));
        Assert.assertEquals(true, conn.isWrapperFor(PoolableConnection.class));
        Assert.assertEquals(true, conn.isWrapperFor(Connection.class));

        Assert.assertEquals("SELECT 1", conn.nativeSQL("SELECT 1"));

        conn.toString();

        conn.close();
        conn.toString();
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

    public void test_prepareStatement_error4() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        {
            SQLException error = null;
            try {
                conn.prepareStatement(null, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                      ResultSet.HOLD_CURSORS_OVER_COMMIT);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error5() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        {
            SQLException error = null;
            try {
                conn.prepareStatement(null, new int[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error6() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1", new int[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error7() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1", new String[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement_error8() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareStatement("SELECT 1", 0);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepareStatement() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                           ResultSet.CONCUR_READ_ONLY);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                           ResultSet.CONCUR_READ_ONLY);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                           ResultSet.CONCUR_READ_ONLY,
                                                           ResultSet.HOLD_CURSORS_OVER_COMMIT);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                           ResultSet.CONCUR_READ_ONLY,
                                                           ResultSet.HOLD_CURSORS_OVER_COMMIT);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", new int[0]);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", new int[0]);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_3() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", new String[0]);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", new String[0]);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_4() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", 0);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT 1", 0);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_preCall_error() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                 ResultSet.HOLD_CURSORS_OVER_COMMIT);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_preCall_error_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_preCall_error_2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.prepareCall("SELECT 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_prepCall() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                      ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                      ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepCall_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        MockPreparedStatement raw = null;
        {
            PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                      ResultSet.CONCUR_READ_ONLY);
            raw = stmt.unwrap(MockPreparedStatement.class);
            stmt.close();
        }
        {
            PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.TYPE_FORWARD_ONLY,
                                                      ResultSet.CONCUR_READ_ONLY);
            Assert.assertEquals(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.getConnectionHolder().toString();
        conn.getConnectionHolder().setLastActiveTimeMillis(0);
        conn.getConnectionHolder().toString();
        conn.getConnectionHolder().getUseCount();
        conn.getConnectionHolder().getTimeMillis();

        conn.close();
    }

    public void test_create() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.createStatement();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_create_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                                     ResultSet.HOLD_CURSORS_OVER_COMMIT);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_create_2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_setAutoCommit() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getAutoCommit();
        conn.setAutoCommit(true);
        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.setAutoCommit(false);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_commit() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.commit();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_rollback() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.rollback();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_rollback_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.rollback(null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_releaseSavepoint_1() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.getConnection().close();
        {
            SQLException error = null;
            try {
                conn.releaseSavepoint(null);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        conn.close();
    }

    public void test_addListenerError() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.addConnectionEventListener(null);
            } catch (IllegalStateException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_addListenerError2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.addStatementEventListener(null);
            } catch (IllegalStateException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_removeConnectionEventListener() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.removeConnectionEventListener(null);
            } catch (IllegalStateException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_removeStatementEventListener() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.removeStatementEventListener(null);
            } catch (IllegalStateException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_checkOpen_error() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.checkOpen();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}
