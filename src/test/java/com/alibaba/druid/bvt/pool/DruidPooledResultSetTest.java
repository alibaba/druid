package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLFeatureNotSupportedException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledResultSet;

public class DruidPooledResultSetTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testWrap() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        ResultSet rs = stmt.executeQuery();

        ResultSet raw = rs.unwrap(ResultSet.class);

        Assert.assertTrue(raw instanceof MockResultSet);

        rs.close();

        conn.close();
    }

    public void test_notSupport() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        DruidPooledResultSet rs = (DruidPooledResultSet) stmt.executeQuery();

        Exception error = null;
        try {
            rs.getObject(1, String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        rs.close();

        conn.close();
    }
    
    public void test_notSupport_1() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        DruidPooledResultSet rs = (DruidPooledResultSet) stmt.executeQuery();
        
        Exception error = null;
        try {
            rs.getObject("1", String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        
        rs.close();
        
        conn.close();
    }
}
