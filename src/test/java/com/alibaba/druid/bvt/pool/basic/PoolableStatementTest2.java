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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledStatement;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class PoolableStatementTest2 extends TestCase {

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
        dataSource.setRemoveAbandoned(true);
        dataSource.setExceptionSorterClassName(null);

        Assert.assertTrue(dataSource.getExceptionSorter() instanceof NullExceptionSorter);
        dataSource.setExceptionSorterClassName("");
        Assert.assertTrue(dataSource.getExceptionSorter() instanceof NullExceptionSorter);

        JdbcStatContext context = new JdbcStatContext();
        context.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(context);
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(true, dataSource.getCreateTimespanNano() > 0);
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_dupClose() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.close();
        stmt.close();
        conn.close();
    }

    public void test_executeUpdate() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("SET @VAR = 1");
        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_error() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeUpdate("SET @VAR = 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_execute_error() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SET @VAR = 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeQuery_error() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeQuery("SELECT 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_setEscapeProcessing() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setEscapeProcessing(true);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.setEscapeProcessing(true);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getMaxFieldSize() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setMaxFieldSize(23);
        Assert.assertEquals(23, stmt.getMaxFieldSize());

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getMaxFieldSize();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setMaxFieldSize(23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_QueryTimeout() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setQueryTimeout(33);
        Assert.assertEquals(33, stmt.getQueryTimeout());

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getQueryTimeout();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setQueryTimeout(23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_MaxRows() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setMaxRows(44);
        Assert.assertEquals(44, stmt.getMaxRows());

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getMaxRows();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setMaxRows(23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_FetchDirection() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setFetchDirection(144);
        Assert.assertEquals(144, stmt.getFetchDirection());

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getFetchDirection();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setFetchDirection(23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_FetchSize() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setFetchSize(144);
        Assert.assertEquals(144, stmt.getFetchSize());

        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getFetchSize();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            SQLException error = null;
            try {
                stmt.setFetchSize(23);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_cancel() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.cancel();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.cancel();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getWarnings() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getWarnings();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getWarnings();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_clearWarnings() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.clearWarnings();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.clearWarnings();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_setCursorName() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setCursorName("c_name");
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.setCursorName("c_name");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getResultSet() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getResultSet();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getResultSet();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getUpdateCount() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getUpdateCount();
        stmt.executeQuery("select 1");
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getUpdateCount();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getMoreResults() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getMoreResults();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getMoreResults();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getResultSetConcurrency() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getResultSetConcurrency();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getResultSetConcurrency();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getResultSetType() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getResultSetType();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getResultSetType();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_addBatch() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.addBatch("select 1");
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.addBatch("select 1");
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_clearBatch() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.clearBatch();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.clearBatch();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeBatch() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeBatch();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeBatch();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getMoreResults_1() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getMoreResults(1);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getMoreResults(1);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getGeneratedKeys() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getGeneratedKeys();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getGeneratedKeys();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getResultSetHoldability() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.getResultSetHoldability();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getResultSetHoldability();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_execute() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("SELECT 1", new String[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", new String[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_execute_1() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("SELECT 1", new int[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", new int[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_execute_2() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("SELECT 1", Statement.NO_GENERATED_KEYS);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", Statement.NO_GENERATED_KEYS);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_1() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SELECT 1", new String[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", new String[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_2() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SELECT 1", new int[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", new int[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_3() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SELECT 1", Statement.NO_GENERATED_KEYS);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute("SELECT 1", Statement.NO_GENERATED_KEYS);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getMeta() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELELCT 1");

        stmt.getMetaData();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getMetaData();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_getParameterMetaData() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELELCT 1");

        stmt.getParameterMetaData();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.getParameterMetaData();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_wasNull() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("SELELCT 1");

        stmt.wasNull();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.wasNull();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");

        stmt.executeQuery();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeQuery();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_4() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");

        stmt.executeQuery();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeUpdate();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_execute_3() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELELCT 1");

        stmt.execute();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.execute();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_clearParameters() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELELCT 1");

        stmt.clearParameters();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.clearParameters();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_addBatch_1() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELELCT 1");

        stmt.addBatch();
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.addBatch();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_5() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SET 1", Statement.RETURN_GENERATED_KEYS);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeUpdate("SET 1", Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_6() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SET 1", new String[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeUpdate("SET 1", new String[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_executeUpdate_7() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("SET 1", new int[0]);
        ((DruidPooledStatement) stmt).getStatement().close();

        {
            SQLException error = null;
            try {
                stmt.executeUpdate("SET 1", new int[0]);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }

    public void test_setPoolable() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();

        stmt.setPoolable(true);

        {
            SQLException error = null;
            try {
                stmt.setPoolable(false);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        stmt.close();
        conn.close();
    }
}
