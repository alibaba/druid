package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigFileGenerator;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoader;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Jonas Yang
 */
public class FileConfigLoaderTest1 extends ConfigFileGenerator {

    @Test
    public void testLoadConfigByFile() {
        FileConfigLoader configLoader = new FileConfigLoader();
        Properties p = configLoader.loadConfig(FileConfigLoader.PROTOCOL_PREFIX + this.filePath);

        Assert.assertNotNull("The properties is null", p);
        Assert.assertEquals("The value is " + p.getProperty("username") + ", not test1", "test1", p.getProperty("username"));
    }

    @Test
    public void testLoadConfigByNotExistFile() {
        FileConfigLoader configLoader = new FileConfigLoader();
        Properties p = configLoader.loadConfig(FileConfigLoader.PROTOCOL_PREFIX + "/test/test/test");

        Assert.assertNull("The properties is not null", p);
    }

}
