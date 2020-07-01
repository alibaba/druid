package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest_fill extends TestCase {

    private DruidDataSource dataSource;

    private int             maxActive = 10;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(maxActive);
        dataSource.setTestOnBorrow(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_fill_0() throws Exception {
        int fillCount = dataSource.fill(3);
        Assert.assertEquals(3, fillCount);
    }

    public void test_fill_1() throws Exception {
        int fillCount = dataSource.fill(1000);
        Assert.assertEquals(maxActive, fillCount);
    }

    public void test_fill_2() throws Exception {
        int fillCount = dataSource.fill(maxActive);
        Assert.assertEquals(maxActive, fillCount);
    }

    public void test_fill_3() throws Exception {
        int fillCount = dataSource.fill();
        Assert.assertEquals(maxActive, fillCount);
    }

    public void test_fill_5() throws Exception {
        Exception error = null;
        try {
            dataSource.fill(-1);
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

}
