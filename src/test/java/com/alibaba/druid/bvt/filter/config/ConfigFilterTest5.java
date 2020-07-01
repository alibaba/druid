package com.alibaba.druid.bvt.filter.config;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigFilter;


public class ConfigFilterTest5 extends TestCase {
    public void test_loadClassPath() throws Exception {
        ConfigFilter filter = new ConfigFilter();
        Assert.assertNotNull(filter.loadConfig("classpath:bvt/config/config-0.properties"));
    }
}
