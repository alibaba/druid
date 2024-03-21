package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;

import junit.framework.TestCase;

public class WallFilterConfigSpiForNullDbTypeTest extends TestCase {

    private DruidDataSource dataSource;
    private WallFilter wallFilter;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:nodb:mem:wall_test;");
        dataSource.setFilters("wall");
        dataSource.init();

        wallFilter = (WallFilter) dataSource.getProxyFilters().get(0);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_wallFilter() throws Exception {
        System.out.println("wallFilter= " + wallFilter);
        System.out.println("wallFilter.getConfig()= " + wallFilter.getConfig());
        System.out.println("wallFilter.getConfig()= " + wallFilter.getProvider().getClass());
        assertNull(wallFilter.getConfig());
        assertTrue(wallFilter.getProvider() instanceof NullWallProvider);
    }
}
