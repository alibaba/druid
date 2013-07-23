package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TimeBetweenLogStatsMillisTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        System.setProperty("druid.timeBetweenLogStatsMillis", "1000");
        
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        System.clearProperty("druid.timeBetweenLogStatsMillis");
    }

    public void test_0() throws Exception {
        dataSource.init();
        Assert.assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
    }
}
