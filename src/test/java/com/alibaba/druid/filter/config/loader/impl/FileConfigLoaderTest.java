package com.alibaba.druid.filter.config.loader.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonas Yang
 */
public class FileConfigLoaderTest {

    @Test
    public void testIsSupported() {
        FileConfigLoader configLoader = new FileConfigLoader();
        boolean isSupported = configLoader.isSupported(FileConfigLoader.PROTOCOL_PREFIX + "/opt/mytest");
        Assert.assertTrue("The value is " + isSupported + ", not true", isSupported);

        isSupported = configLoader.isSupported("jdbc:/opt/mytest");
        Assert.assertFalse("The value is " + isSupported + ", not false", isSupported);
    }

    @Test
    public void testGetFilePath() {
        FileConfigLoader configLoader = new FileConfigLoader();
        String filePath = configLoader.getFilePath(FileConfigLoader.PROTOCOL_PREFIX + "/opt/test/test.properties");
        Assert.assertEquals("The path is " + filePath, filePath, "/opt/test/test.properties");
    }
}
