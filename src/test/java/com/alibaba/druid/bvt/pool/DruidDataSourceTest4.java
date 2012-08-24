package com.alibaba.druid.bvt.pool;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试maxActive < 0
 * 
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSourceTest4 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setMaxActive(-1);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.init();
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
