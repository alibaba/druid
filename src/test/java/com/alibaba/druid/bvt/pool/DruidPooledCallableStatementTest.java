package com.alibaba.druid.bvt.pool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledCallableStatement;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;

public class DruidPooledCallableStatementTest extends TestCase {

    private DruidDataSource dataSource;

    private boolean         throwError = true;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

        dataSource.getProxyFilters().add(new FilterAdapter() {

            public boolean callableStatement_wasNull(FilterChain chain, CallableStatementProxy statement)
                                                                                                         throws SQLException {
                if (throwError) {
                throw new SQLException();
                } else {
                    return chain.callableStatement_wasNull(statement);
                }
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_wasNull_noerror() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select 1");
        stmt.execute();

        throwError = false;
        stmt.wasNull();

        Assert.assertEquals(0, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
    
    public void test_wasNull_error() throws Exception {
        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select 1");
        stmt.execute();
        
        Assert.assertEquals(0, dataSource.getErrorCount());
        
        Exception error = null;
        try {
            stmt.wasNull();
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        Assert.assertEquals(1, dataSource.getErrorCount());
        
        stmt.close();
        conn.close();
        
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_getObject() throws Exception {
        Connection conn = dataSource.getConnection();
        DruidPooledCallableStatement stmt = (DruidPooledCallableStatement) conn.prepareCall("select 1");
        stmt.execute();

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getObject(1, String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(0, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_getObject_1() throws Exception {
        Connection conn = dataSource.getConnection();
        DruidPooledCallableStatement stmt = (DruidPooledCallableStatement) conn.prepareCall("select 1");
        stmt.execute();

        Assert.assertEquals(0, dataSource.getErrorCount());

        Exception error = null;
        try {
            stmt.getObject("1", String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(0, dataSource.getErrorCount());

        stmt.close();
        conn.close();

        Assert.assertEquals(1, dataSource.getPoolingCount());
    }

    public void test_wrap() throws Exception {

        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select 1");

        Assert.assertNotNull(stmt.unwrap(CallableStatement.class));
        Assert.assertEquals(MockCallableStatement.class, stmt.unwrap(CallableStatement.class).getClass());

        stmt.close();
        conn.close();
    }

    public void test_wrap_1() throws Exception {

        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select 1");

        Assert.assertNotNull(stmt.unwrap(PreparedStatement.class));
        Assert.assertEquals(MockCallableStatement.class, stmt.unwrap(CallableStatement.class).getClass());

        stmt.close();
        conn.close();
    }

    public void test_wrap_2() throws Exception {
        dataSource.getProxyFilters().clear();

        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall("select 1");

        Assert.assertNotNull(stmt.unwrap(PreparedStatement.class));
        Assert.assertEquals(MockCallableStatement.class, stmt.unwrap(CallableStatement.class).getClass());

        stmt.close();
        conn.close();
    }
}
