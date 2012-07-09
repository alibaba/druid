package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TestH2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(10);
        
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_h2() throws Exception {
        Assert.assertSame(JdbcUtils.H2, dataSource.getDbType());
        
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT ?");

        stmt.setString(1, "xxxx");

        ResultSet rs = stmt.executeQuery();

        rs.next();
        Assert.assertEquals("xxxx", rs.getString(1));

        rs.close();

        stmt.close();

        conn.close();
    }
}
