package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_loadSysPropertyFilter extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:wrap-jdbc:filters=stat,log4j:jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        System.clearProperty("druid.filters");
    }

    public void test_autoCommit() throws Exception {
        dataSource.init();

        assertEquals(2, dataSource.getProxyFilters().size());
    }
}
