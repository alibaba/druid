package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

public class UnwrapTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setInitialSize(1);
        dataSource.setValidationQuery("select 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setQueryTimeout(100);

        dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_unwrap() throws Exception {
        Connection conn = dataSource.getConnection();

        Assert.assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
        Assert.assertNotNull(conn.unwrap(DruidPooledConnection.class));
        Assert.assertSame(conn, conn.unwrap(DruidPooledConnection.class));

        Assert.assertTrue(conn.isWrapperFor(MockConnection.class));
        Assert.assertNotNull(conn.unwrap(MockConnection.class));
        
        PreparedStatement stmt = conn.prepareStatement("select ?");

        Assert.assertTrue(stmt.isWrapperFor(Statement.class));
        Assert.assertNotNull(stmt.unwrap(Statement.class));

        Assert.assertTrue(stmt.isWrapperFor(PreparedStatement.class));
        Assert.assertNotNull(stmt.unwrap(PreparedStatement.class));

        Assert.assertTrue(stmt.isWrapperFor(StatementProxy.class));
        Assert.assertNotNull(stmt.unwrap(StatementProxy.class));

        Assert.assertTrue(stmt.isWrapperFor(PreparedStatementProxy.class));
        Assert.assertNotNull(stmt.unwrap(PreparedStatementProxy.class));

        Assert.assertTrue(stmt.isWrapperFor(PreparedStatementProxyImpl.class));
        Assert.assertNotNull(stmt.unwrap(PreparedStatementProxyImpl.class));
        
        Assert.assertTrue(stmt.isWrapperFor(MockPreparedStatement.class));
        Assert.assertNotNull(stmt.unwrap(MockPreparedStatement.class));

        stmt.setObject(1, "aaa");
        ResultSet rs = stmt.executeQuery();

        Assert.assertTrue(rs.isWrapperFor(ResultSet.class));
        Assert.assertNotNull(rs.unwrap(ResultSet.class));

        Assert.assertTrue(rs.isWrapperFor(ResultSetProxy.class));
        Assert.assertNotNull(rs.unwrap(ResultSetProxy.class));
        
        Assert.assertTrue(rs.isWrapperFor(ResultSetProxyImpl.class));
        Assert.assertNotNull(rs.unwrap(ResultSetProxyImpl.class));
        
        Assert.assertTrue(rs.isWrapperFor(MockResultSet.class));
        Assert.assertNotNull(rs.unwrap(MockResultSet.class));
        
        rs.close();

        stmt.close();

        conn.close();
    }
}
