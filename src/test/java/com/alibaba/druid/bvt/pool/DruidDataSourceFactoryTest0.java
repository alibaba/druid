package com.alibaba.druid.bvt.pool;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DruidDataSourceFactoryTest0 extends TestCase {

    public void test_factory_null() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        Assert.assertNull(factory.getObjectInstance(null, null, null, null));
    }
    
    public void test_factory_null_1() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        Assert.assertNull(factory.getObjectInstance(new Object(), null, null, null));
    }
}
