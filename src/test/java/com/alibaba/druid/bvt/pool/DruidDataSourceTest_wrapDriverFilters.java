package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_wrapDriverFilters extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        System.setProperty("druid.filters", "stat,log4j");
        
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        System.clearProperty("druid.filters");
    }

    public void test_autoCommit() throws Exception {
       dataSource.init();
       
       Assert.assertEquals(2, dataSource.getProxyFilters().size());
    }
}
