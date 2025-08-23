package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;


public class RegisterTest extends TestCase {
    public void test() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.init();

        assertTrue(dataSource.isMbeanRegistered());

        dataSource.registerMbean();
        assertTrue(dataSource.isMbeanRegistered());

        dataSource.unregisterMbean();
        assertFalse(dataSource.isMbeanRegistered());
        assertFalse(dataSource.isMbeanRegistered());

        dataSource.close();
    }
}
