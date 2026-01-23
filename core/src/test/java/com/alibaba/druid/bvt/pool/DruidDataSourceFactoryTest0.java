package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import junit.framework.TestCase;

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
