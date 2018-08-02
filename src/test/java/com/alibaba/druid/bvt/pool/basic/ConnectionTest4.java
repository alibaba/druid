/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionTest4 extends PoolTestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

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
        super.tearDown();
    }

    public void test_basic() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        Assert.assertEquals(null, conn.unwrap(Date.class));
        Assert.assertEquals(null, conn.unwrap(null));
        Assert.assertEquals(((ConnectionProxy) conn.getConnection()).getRawObject(), conn.unwrap(Connection.class));

        Assert.assertEquals(false, conn.isWrapperFor(null));
        Assert.assertEquals(true, conn.isWrapperFor(DruidPooledConnection.class));
        Assert.assertEquals(true, conn.isWrapperFor(Connection.class));

        Assert.assertEquals("SELECT 1", conn.nativeSQL("SELECT 1"));

        conn.toString();

        conn.close();
        conn.toString();
    }

    public void test_prepareStatement_error() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
            Assert.assertSame(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_1() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
            Assert.assertSame(raw, stmt.unwrap(MockPreparedStatement.class));
            stmt.close();
        }

        conn.close();
    }

    public void test_prepareStatement_2() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

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
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();
        {
            Exception error = null;
            try {
                conn.checkState();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}
