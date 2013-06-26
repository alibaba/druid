package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TimeBetweenLogStatsMillisTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTimeBetweenLogStatsMillis(1000);
        // dataSource.setFilters("log4j");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        Assert.assertEquals(true, dataSource.isResetStatEnable());
        dataSource.init();
        Assert.assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        dataSource.resetStat();
        Assert.assertEquals(0, dataSource.getResetCount());
        dataSource.setConnectionProperties("druid.resetStatEnable=true");
        Assert.assertEquals(true, dataSource.isResetStatEnable());
        
        dataSource.setConnectionProperties("druid.resetStatEnable=false");
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        
        dataSource.setConnectionProperties("druid.resetStatEnable=xxx");
    }
}
