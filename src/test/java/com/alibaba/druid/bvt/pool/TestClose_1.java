package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class TestClose_1 extends TestCase {

    public void test_close() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUrl("jdbc:mock:xxx");

        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertEquals(false, rs.isClosed());
        
        Assert.assertEquals(false, stmt.isClosed());
        stmt.close();
        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());
        
        PreparedStatement stmt2 = conn.prepareStatement(sql);
        Assert.assertEquals(false, stmt.isClosed());
        
        Assert.assertEquals(stmt, stmt2);

        conn.close();

        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();
        
        dataSource.close();
    }
}
