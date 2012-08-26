package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

public class DruidPooledPreparedStatementTest1 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement)
                                                                                                         throws SQLException {
                throw new SQLException();
            }

            @Override
            public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement)
                                                                                                           throws SQLException {
                throw new SQLException();
            }

            public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                                     throws SQLException {
                throw new SQLException();
            }

            public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
                                                                                                              throws SQLException {
                throw new SQLException();
            }

            @Override
            public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
                throw new SQLException();
            }
            
            @Override
            public ParameterMetaData preparedStatement_getParameterMetaData(FilterChain chain, PreparedStatementProxy statement)
                                                                                                                                throws SQLException {
                throw new SQLException();
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_execute_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.execute();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeQuery_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeQuery();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeUpdate_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.executeUpdate();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_clearParameter_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.clearParameters();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(1, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_executeBatch_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");

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
    }
    
    public void test_getParameterMetaData_error() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.getParameterMetaData();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}
