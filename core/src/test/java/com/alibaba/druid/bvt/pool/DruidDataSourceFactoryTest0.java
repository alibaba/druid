package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceFactoryTest0 {
    @Test
    public void test_factory_null() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        assertNull(factory.getObjectInstance(null, null, null, null));
    }

    @Test
    public void test_factory_null_1() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        assertNull(factory.getObjectInstance(new Object(), null, null, null));
    }
}
