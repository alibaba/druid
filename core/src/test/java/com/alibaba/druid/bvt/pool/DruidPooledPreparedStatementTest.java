package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.ResultSet;

import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;

public class DruidPooledPreparedStatementTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_hitCount() throws Exception {
        String sql = "select ?";
        {
            Connection conn = dataSource.getConnection();
            DruidPooledPreparedStatement stmt = (DruidPooledPreparedStatement) conn.prepareStatement(sql);

            assertEquals(0, stmt.getFetchSize());
            assertEquals(0, stmt.getFetchDirection());
            assertEquals(0, stmt.getMaxRows());
            assertEquals(0, stmt.getMaxFieldSize());
            assertEquals(0, stmt.getQueryTimeout());
            assertEquals(sql, stmt.getSql());

            stmt.setFetchSize(1);
            stmt.setFetchDirection(2);
            stmt.setMaxRows(3);
            stmt.setMaxFieldSize(4);
            stmt.setQueryTimeout(5);

            assertEquals(1, stmt.getFetchSize());
            assertEquals(2, stmt.getFetchDirection());
            assertEquals(3, stmt.getMaxRows());
            assertEquals(4, stmt.getMaxFieldSize());
            assertEquals(5, stmt.getQueryTimeout());

            stmt.setString(1, "xx");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertFalse(rs.next());
            rs.close();
            conn.close();

            assertEquals(0, stmt.getHitCount());
        }
        {
            Connection conn = dataSource.getConnection();
            DruidPooledPreparedStatement stmt = (DruidPooledPreparedStatement) conn.prepareStatement(sql);

            assertEquals(0, stmt.getFetchSize());
            assertEquals(0, stmt.getFetchDirection());
            assertEquals(0, stmt.getMaxRows());
            assertEquals(0, stmt.getMaxFieldSize());
            assertEquals(0, stmt.getQueryTimeout());

            stmt.setString(1, "xx");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertFalse(rs.next());
            rs.close();
            conn.close();

            assertEquals(1, stmt.getHitCount());
            assertNotNull(stmt.getKey());
        }
    }
}
