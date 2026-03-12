package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_loadSysPropertyFilter {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:wrap-jdbc:filters=stat,log4j:jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
        System.clearProperty("druid.filters");
    }

    @Test
    public void test_autoCommit() throws Exception {
        dataSource.init();

        assertEquals(2, dataSource.getProxyFilters().size());
    }
}
