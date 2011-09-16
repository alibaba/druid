package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableStatement;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class PoolableStatementTest2 extends TestCase {

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
        dataSource.setFilters("stat");
        dataSource.setRemoveAbandoned(true);
        dataSource.setExceptionSorterClassName(null);

        Assert.assertTrue(dataSource.getExceptionSoter() instanceof NullExceptionSorter);
        dataSource.setExceptionSorterClassName("");
        Assert.assertTrue(dataSource.getExceptionSoter() instanceof NullExceptionSorter);
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(true, dataSource.getCreateTimespanNano() > 0);
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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

        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();

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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
        ((PoolableStatement) stmt).getStatement().close();
        
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
    
}
