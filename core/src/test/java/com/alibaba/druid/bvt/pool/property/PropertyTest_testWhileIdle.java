package com.alibaba.druid.bvt.pool.property;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest_testWhileIdle {
    private DruidDataSource dataSource;

    @Test
    public void test_true() {
        System.setProperty("druid.testWhileIdle", "true");
        dataSource = new DruidDataSource();
        assertTrue(dataSource.isTestWhileIdle());
    }

    @Test
    public void test_false() {
        System.setProperty("druid.testWhileIdle", "false");
        dataSource = new DruidDataSource();
        assertFalse(dataSource.isTestWhileIdle());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        System.clearProperty("druid.testWhileIdle");
        JdbcUtils.close(dataSource);
    }
}
