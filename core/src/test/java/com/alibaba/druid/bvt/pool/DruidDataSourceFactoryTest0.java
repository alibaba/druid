package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DruidDataSourceFactoryTest0 extends TestCase {
    public void test_factory_null() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        assertNull(factory.getObjectInstance(null, null, null, null));
    }

    public void test_factory_null_1() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        assertNull(factory.getObjectInstance(new Object(), null, null, null));
    }
}
