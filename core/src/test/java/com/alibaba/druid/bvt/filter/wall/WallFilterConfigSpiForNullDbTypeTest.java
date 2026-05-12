package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WallFilterConfigSpiForNullDbTypeTest {
    private DruidDataSource dataSource;
    private WallFilter wallFilter;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:nodb:mem:wall_test;");
        dataSource.setFilters("wall");
        dataSource.init();

        wallFilter = (WallFilter) dataSource.getProxyFilters().get(0);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_wallFilter() throws Exception {
        // With correct SPI ordering, Test02WallProviderCreator (order=100) takes
        // precedence over Test01WallProviderCreator (order=200)
        assertNotNull(wallFilter.getConfig());
        assertTrue(wallFilter.getProvider() instanceof NoMatchDbWallProvider);
    }
}
