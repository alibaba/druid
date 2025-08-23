package com.alibaba.druid.bvt.pool.property;

import static org.junit.Assert.*;


import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_testOnBorrow extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.testOnBorrow", "true");
        dataSource = new DruidDataSource();
        assertTrue(dataSource.isTestOnBorrow());
    }

    public void test_false() {
        System.setProperty("druid.testOnBorrow", "false");
        dataSource = new DruidDataSource();
        assertFalse(dataSource.isTestOnBorrow());

        assertNull(dataSource.getWallStatMap());
    }

    protected void tearDown() throws Exception {
        System.clearProperty("druid.testOnBorrow");
        JdbcUtils.close(dataSource);
    }
}
