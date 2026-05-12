package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigFilterTest5 {
    @Test
    public void test_loadClassPath() throws Exception {
        ConfigFilter filter = new ConfigFilter();
        assertNotNull(filter.loadConfig("classpath:bvt/config/config-0.properties"));
    }
}
