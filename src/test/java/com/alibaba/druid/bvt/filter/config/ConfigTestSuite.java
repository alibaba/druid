package com.alibaba.druid.bvt.filter.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.alibaba.druid.filter.config.ConfigFilterTest;
import com.alibaba.druid.filter.config.loader.ConfigLoaderFactoryTest;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoaderTest;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoaderTest1;
import com.alibaba.druid.filter.config.loader.impl.HttpConfigLoaderTest;

/**
 * @author Jonas Yang
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigLoaderFactoryTest.class,
        FileConfigLoaderTest.class,
        FileConfigLoaderTest1.class,
        HttpConfigLoaderTest.class,
        ConfigFilterTest.class
})
public class ConfigTestSuite {
}
