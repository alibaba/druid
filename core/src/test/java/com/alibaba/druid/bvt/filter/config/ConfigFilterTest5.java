package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigFilter;
import junit.framework.TestCase;

public class ConfigFilterTest5 extends TestCase {
    public void test_loadClassPath() throws Exception {
        ConfigFilter filter = new ConfigFilter();
        assertNotNull(filter.loadConfig("classpath:bvt/config/config-0.properties"));
    }
}
