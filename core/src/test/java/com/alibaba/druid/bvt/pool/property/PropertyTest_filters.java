package com.alibaba.druid.bvt.pool.property;

import static org.junit.Assert.*;


import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_filters extends PoolTestCase {
    private DruidDataSource dataSource;

    public void test_stat() {
        System.setProperty("druid.filters", "stat");
        dataSource = new DruidDataSource();
        assertEquals(1, dataSource.getProxyFilters().size());
        assertEquals("com.alibaba.druid.filter.stat.StatFilter", dataSource.getFilterClassNames().get(0));
    }

    public void test_false() {
        System.setProperty("druid.filters", "");
        dataSource = new DruidDataSource();
        assertEquals(0, dataSource.getProxyFilters().size());

        assertNull(dataSource.getWallStatMap());
    }

    protected void tearDown() throws Exception {
        System.clearProperty("druid.filters");
        JdbcUtils.close(dataSource);

        super.tearDown();
    }
}
