package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import junit.framework.TestCase;


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
        assertEquals(true, dataSource.isResetStatEnable());
        dataSource.init();
        assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
        assertEquals(false, dataSource.isResetStatEnable());
        dataSource.resetStat();
        assertEquals(0, dataSource.getResetCount());
        dataSource.setConnectionProperties("druid.resetStatEnable=true");
        assertEquals(true, dataSource.isResetStatEnable());

        dataSource.setConnectionProperties("druid.resetStatEnable=false");
        assertEquals(false, dataSource.isResetStatEnable());

        dataSource.setConnectionProperties("druid.resetStatEnable=xxx");
    }
}
