package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigFilterTest;
import com.alibaba.druid.filter.config.loader.ConfigLoaderFactoryTest;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoaderTest;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoaderTest1;
import com.alibaba.druid.filter.config.loader.impl.HttpConfigLoaderTest;
import com.alibaba.druid.filter.config.loader.impl.HttpConfigLoaderTest1;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Jonas Yang
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigLoaderFactoryTest.class,
        FileConfigLoaderTest.class,
        FileConfigLoaderTest1.class,
        HttpConfigLoaderTest.class,
        HttpConfigLoaderTest1.class,
        ConfigFilterTest.class
})
public class ConfigTestSuite {
}
