package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;

public class TestStat extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setName("com.alibaba.dragoon.monitor");
        dataSource.setMinIdle(0);
        dataSource.setPoolPreparedStatements(false);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_stat() throws Exception {

        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        conn.close();

        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        dataSource.shrink();

        JdbcStatManager.getInstance().getDataSourceList();
        Assert.assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

    }
}
