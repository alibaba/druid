package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TimeBetweenLogStatsMillisTest3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        System.setProperty("druid.timeBetweenLogStatsMillis", "10");

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        System.clearProperty("druid.timeBetweenLogStatsMillis");
    }

    public void test_0() throws Exception {
        dataSource.init();
        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select ?");
            stmt.setString(1, "aaa");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();
            
            Thread.sleep(10);
        }
        Assert.assertEquals(10, dataSource.getTimeBetweenLogStatsMillis());
    }
}
