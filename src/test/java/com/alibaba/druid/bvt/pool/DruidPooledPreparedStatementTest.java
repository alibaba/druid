package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.ResultSet;

import junit.framework.TestCase;

import org.junit.Assert;

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
            
            Assert.assertEquals(0, stmt.getFetchSize());
            Assert.assertEquals(0, stmt.getFetchDirection());
            Assert.assertEquals(0, stmt.getMaxRows());
            Assert.assertEquals(0, stmt.getMaxFieldSize());
            Assert.assertEquals(0, stmt.getQueryTimeout());
            Assert.assertEquals(sql, stmt.getSql());

            stmt.setFetchSize(1);
            stmt.setFetchDirection(2);
            stmt.setMaxRows(3);
            stmt.setMaxFieldSize(4);
            stmt.setQueryTimeout(5);
            
            Assert.assertEquals(1, stmt.getFetchSize());
            Assert.assertEquals(2, stmt.getFetchDirection());
            Assert.assertEquals(3, stmt.getMaxRows());
            Assert.assertEquals(4, stmt.getMaxFieldSize());
            Assert.assertEquals(5, stmt.getQueryTimeout());
            
            stmt.setString(1, "xx");
            ResultSet rs = stmt.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertFalse(rs.next());
            rs.close();
            conn.close();

            Assert.assertEquals(0, stmt.getHitCount());
        }
        {
            Connection conn = dataSource.getConnection();
            DruidPooledPreparedStatement stmt = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
            
            Assert.assertEquals(0, stmt.getFetchSize());
            Assert.assertEquals(0, stmt.getFetchDirection());
            Assert.assertEquals(0, stmt.getMaxRows());
            Assert.assertEquals(0, stmt.getMaxFieldSize());
            Assert.assertEquals(0, stmt.getQueryTimeout());
            
            stmt.setString(1, "xx");
            ResultSet rs = stmt.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertFalse(rs.next());
            rs.close();
            conn.close();
            
            Assert.assertEquals(1, stmt.getHitCount());
            Assert.assertNotNull(stmt.getKey());
        }
    }
}
