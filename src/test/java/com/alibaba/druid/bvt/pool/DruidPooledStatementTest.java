package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledStatement;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

public class DruidPooledStatementTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new ErrorFilter());
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_executeQuery_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeQuery("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_executeUpdate_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeUpdate("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_executeUpdate_error_1() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeUpdate("select 1", Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_executeUpdate_error_2() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeUpdate("select 1", new int[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_executeUpdate_error_3() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeUpdate("select 1", new String[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_execute_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.execute("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_execute_error_1() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.execute("select 1", Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_execute_error_2() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.execute("select 1", new int[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_execute_error_3() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.execute("select 1", new String[0]);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_getMaxFieldSize_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getMaxFieldSize();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_setMaxFieldSize_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.setMaxFieldSize(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_getMaxRows_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getMaxRows();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_setMaxRows_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.setMaxRows(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_setEscapeProcessing_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.setEscapeProcessing(true);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_getQueryTimeout_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getQueryTimeout();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_setQueryTimeout_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.setQueryTimeout(-1);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_cancel_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.cancel();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_getWarnings_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getWarnings();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_clearWarnings_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.clearWarnings();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_setCursorName_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.setCursorName(null);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    public void test_getResultSet_error() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getResultSet();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getDataSourceStat().getResultSetStat().getOpenCount());
    }
    
    public void test_getUpdateCount_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getUpdateCount();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getMoreResults_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getMoreResults();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_setFetchDirection_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.setFetchDirection(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getFetchDirection_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getFetchDirection();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_setFetchSize_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.setFetchSize(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getFetchSize_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getFetchSize();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getResultSetConcurrency_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getResultSetConcurrency();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getResultSetType_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getResultSetType();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_addBatch_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.addBatch("select 1");
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_clearBatch_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.clearBatch();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_executeBatch_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.executeBatch();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getMoreResults_error_1() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getMoreResults(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getGeneratedKeys_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getGeneratedKeys();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_getResultSetHoldability_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getResultSetHoldability();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_closeOnCompletion_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        DruidPooledStatement stmt = (DruidPooledStatement) conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.closeOnCompletion();
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
    
    public void test_isCloseOnCompletion_error() throws Exception {
        Connection conn = dataSource.getConnection();
        
        DruidPooledStatement stmt = (DruidPooledStatement) conn.createStatement();
        
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.isCloseOnCompletion();
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
    }

    private final class ErrorFilter extends FilterAdapter {

        @Override
        public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                             throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql,
                                           int autoGeneratedKeys) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                        throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql,
                                           String columnNames[]) throws SQLException {
            throw new SQLException();
        }

        @Override
        public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
            throw new SQLException();
        }

        @Override
        public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                        throws SQLException {
            throw new SQLException();
        }

        @Override
        public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                      throws SQLException {
            throw new SQLException();
        }

        @Override
        public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                       throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getMaxFieldSize(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getMaxRows(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setMaxFieldSize(FilterChain chain, StatementProxy statement, int max) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setMaxRows(FilterChain chain, StatementProxy statement, int max) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setEscapeProcessing(FilterChain chain, StatementProxy statement, boolean enable)
                                                                                                              throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getQueryTimeout(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setQueryTimeout(FilterChain chain, StatementProxy statement, int seconds)
                                                                                                       throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_cancel(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public SQLWarning statement_getWarnings(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_clearWarnings(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setCursorName(FilterChain chain, StatementProxy statement, String name)
                                                                                                     throws SQLException {
            throw new SQLException();
        }

        @Override
        public ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getUpdateCount(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public boolean statement_getMoreResults(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setFetchDirection(FilterChain chain, StatementProxy statement, int value)
                                                                                                       throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getFetchDirection(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getFetchSize(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_setFetchSize(FilterChain chain, StatementProxy statement, int value) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getResultSetConcurrency(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int statement_getResultSetType(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
            throw new SQLException();
        }

        @Override
        public void statement_clearBatch(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }

        @Override
        public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }
        
        @Override
        public boolean statement_getMoreResults(FilterChain chain, StatementProxy statement, int current)
                throws SQLException {
            throw new SQLException();
        }
        
        @Override
        public ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }
        
        @Override
        public int statement_getResultSetHoldability(FilterChain chain, StatementProxy statement) throws SQLException {
            throw new SQLException();
        }
    }
}
