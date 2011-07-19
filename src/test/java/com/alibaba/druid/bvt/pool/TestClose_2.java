package com.alibaba.druid.bvt.pool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class TestClose_2 extends TestCase {

    public void test_close() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUrl("jdbc:mock:xxx");

        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertEquals(false, rs.isClosed());
        
        Assert.assertEquals(false, stmt.isClosed());
        stmt.close();
        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());
        
        CallableStatement stmt2 = conn.prepareCall(sql);
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
