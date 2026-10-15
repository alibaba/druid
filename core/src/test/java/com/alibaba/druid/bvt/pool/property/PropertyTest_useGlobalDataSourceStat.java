package com.alibaba.druid.bvt.pool.property;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest_useGlobalDataSourceStat {
    private DruidDataSource dataSource;

    @Test
    public void test_true() {
        System.setProperty("druid.useGlobalDataSourceStat", "true");
        dataSource = new DruidDataSource();
        assertTrue(dataSource.isUseGlobalDataSourceStat());
    }

    @Test
    public void test_false() {
        System.setProperty("druid.useGlobalDataSourceStat", "false");
        dataSource = new DruidDataSource();
        assertFalse(dataSource.isUseGlobalDataSourceStat());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        System.clearProperty("druid.useGlobalDataSourceStat");
        JdbcUtils.close(dataSource);
    }
}
