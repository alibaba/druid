package com.alibaba.druid.bvt.pool;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UnwrapTest {
    private DruidDataSource dataSource;

    @BeforeEach
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

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_unwrap() throws Exception {
        Connection conn = dataSource.getConnection();

        assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
        assertNotNull(conn.unwrap(DruidPooledConnection.class));
        assertSame(conn, conn.unwrap(DruidPooledConnection.class));

        assertTrue(conn.isWrapperFor(MockConnection.class));
        assertNotNull(conn.unwrap(MockConnection.class));

        PreparedStatement stmt = conn.prepareStatement("select ?");

        assertTrue(stmt.isWrapperFor(Statement.class));
        assertNotNull(stmt.unwrap(Statement.class));

        assertTrue(stmt.isWrapperFor(PreparedStatement.class));
        assertNotNull(stmt.unwrap(PreparedStatement.class));

        assertTrue(stmt.isWrapperFor(StatementProxy.class));
        assertNotNull(stmt.unwrap(StatementProxy.class));

        assertTrue(stmt.isWrapperFor(PreparedStatementProxy.class));
        assertNotNull(stmt.unwrap(PreparedStatementProxy.class));

        assertTrue(stmt.isWrapperFor(PreparedStatementProxyImpl.class));
        assertNotNull(stmt.unwrap(PreparedStatementProxyImpl.class));

        assertTrue(stmt.isWrapperFor(MockPreparedStatement.class));
        assertNotNull(stmt.unwrap(MockPreparedStatement.class));

        stmt.setObject(1, "aaa");
        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.isWrapperFor(ResultSet.class));
        assertNotNull(rs.unwrap(ResultSet.class));

        assertTrue(rs.isWrapperFor(ResultSetProxy.class));
        assertNotNull(rs.unwrap(ResultSetProxy.class));

        assertTrue(rs.isWrapperFor(ResultSetProxyImpl.class));
        assertNotNull(rs.unwrap(ResultSetProxyImpl.class));

        assertTrue(rs.isWrapperFor(MockResultSet.class));
        assertNotNull(rs.unwrap(MockResultSet.class));

        rs.close();

        stmt.close();

        conn.close();
    }
}
