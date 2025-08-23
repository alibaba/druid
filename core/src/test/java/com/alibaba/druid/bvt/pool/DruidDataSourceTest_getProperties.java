package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getProperties extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.getConnectProperties().put("user", "jobs");
        dataSource.getConnectProperties().put("password", "xxx");
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        assertEquals(-1, dataSource.getProperties().indexOf("xxx"));
        assertEquals(true, dataSource.getProperties().indexOf("******") != -1);
    }
}
