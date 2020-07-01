package com.alibaba.druid.bvt.pool;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试initialSize > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_maxActive4 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setMinIdle(4);
        dataSource.setMaxActive(8);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        dataSource.init();
        Exception error = null;
        try {
            dataSource.setMaxActive(2);
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
