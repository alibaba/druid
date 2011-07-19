package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;

public class Case3 extends TestCase {

    // public void test_0() throws Exception {
    // DruidDataSource dataSource = new DruidDataSource();
    // dataSource.setUrl("jdbc:mock:xxx");
    // dataSource.setPoolPreparedStatements(true);
    // dataSource.close();
    // }
    //
    // public void test_1() throws Exception {
    // DruidDataSource dataSource = new DruidDataSource();
    // dataSource.setUrl("jdbc:mock:xxx");
    //
    // Connection conn = dataSource.getConnection();
    // Statement stmt = conn.createStatement();
    // ResultSet rs = stmt.executeQuery("SELECT 1");
    // rs.next();
    //
    // conn.close();
    //
    // Assert.assertEquals(true, stmt.isClosed());
    // Assert.assertEquals(true, rs.isClosed());
    //
    // rs.close();
    // stmt.close();
    //
    // dataSource.close();
    // }

    public void test_2() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        MockStatement mockStmt = stmt.unwrap(MockStatement.class);
        Assert.assertEquals(false, mockStmt.isClosed());

        conn.close();
        
        Assert.assertEquals(true, mockStmt.isClosed());

        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        stmt.execute("SELECT 1");

        dataSource.close();
    }
}
