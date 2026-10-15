package com.alibaba.druid.bvt.pool.property;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest_testOnBorrow {
    private DruidDataSource dataSource;

    @Test
    public void test_true() {
        System.setProperty("druid.testOnBorrow", "true");
        dataSource = new DruidDataSource();
        assertTrue(dataSource.isTestOnBorrow());
    }

    @Test
    public void test_false() {
        System.setProperty("druid.testOnBorrow", "false");
        dataSource = new DruidDataSource();
        assertFalse(dataSource.isTestOnBorrow());

        assertNull(dataSource.getWallStatMap());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        System.clearProperty("druid.testOnBorrow");
        JdbcUtils.close(dataSource);
    }
}
