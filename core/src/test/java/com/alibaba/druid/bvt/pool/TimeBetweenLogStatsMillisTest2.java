package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBetweenLogStatsMillisTest2 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        System.setProperty("druid.timeBetweenLogStatsMillis", "1000");

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        System.clearProperty("druid.timeBetweenLogStatsMillis");
    }

    @Test
    public void test_0() throws Exception {
        dataSource.init();
        assertEquals(1000, dataSource.getTimeBetweenLogStatsMillis());
    }
}
