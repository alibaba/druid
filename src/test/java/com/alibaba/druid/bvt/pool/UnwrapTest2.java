package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxyImpl;

public class UnwrapTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setInitialSize(1);
        dataSource.setValidationQuery("select 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setQueryTimeout(100);

        dataSource.setFilters("mergeStat");
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

        Statement stmt = conn.createStatement();

        Assert.assertTrue(stmt.isWrapperFor(Statement.class));
        Assert.assertNotNull(stmt.unwrap(Statement.class));

        Assert.assertTrue(stmt.isWrapperFor(StatementProxy.class));
        Assert.assertNotNull(stmt.unwrap(StatementProxy.class));

        Assert.assertTrue(stmt.isWrapperFor(StatementProxyImpl.class));
        Assert.assertNotNull(stmt.unwrap(StatementProxyImpl.class));

        Assert.assertTrue(stmt.isWrapperFor(MockStatement.class));
        Assert.assertNotNull(stmt.unwrap(MockStatement.class));

        ResultSet rs = stmt.executeQuery("select 1");

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
