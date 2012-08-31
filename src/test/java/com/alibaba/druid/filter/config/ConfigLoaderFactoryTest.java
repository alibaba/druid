package com.alibaba.druid.filter.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Jonas Yang
 */
public class ConfigLoaderFactoryTest {

    @Test
    public void testInitConfigLoader() {
        ConfigLoader[] configLoaders = ConfigLoaderFactory.getConfigLoaders();
        Assert.assertTrue("The size is " + configLoaders.length + " , not 2", configLoaders.length == 2);
    }

    @Test
    public void testGetConfigLoader() {
        ConfigLoader configLoader = ConfigLoaderFactory.getConfigLoader("abcde");
        Assert.assertEquals("The config is " + configLoader, null, configLoader);
    }

    @Test
    public void testAddConfigLoader() {
        int size = ConfigLoaderFactory.getConfigLoaders().length;

        ConfigLoaderFactory.addConfigLoader(new ConfigLoader() {
            @Override
            public String getId() {
                return "My Test";  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Properties loadConfig(String protocol) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isSupported(String protocol) {
                if (protocol.startsWith("abc")) {
                    return true;
                }
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        Assert.assertEquals("The size is " + ConfigLoaderFactory.getConfigLoaders().length, ConfigLoaderFactory.getConfigLoaders().length, size + 1);

        ConfigLoader configLoader = ConfigLoaderFactory.getConfigLoader("abc");
        Assert.assertNotNull("The config loader is null", configLoader);
        Assert.assertEquals("The config loader's id is " + configLoader.getId(), "My Test", configLoader.getId());
    }
}
