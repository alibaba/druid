package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;


import com.alibaba.druid.wall.WallConfig;

import junit.framework.TestCase;

public class WallConfigTest extends TestCase {
    public void test_selectAllow() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.selelctAllow", "true");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertTrue(config.isSelectAllow());
    }

    public void test_selectAllow_false() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.selelctAllow", "false");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertFalse(config.isSelectAllow());
    }

    public void test_deleteAllow() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.deleteAllow", "true");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertTrue(config.isDeleteAllow());
    }

    public void test_deleteAllow_false() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.deleteAllow", "false");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertFalse(config.isDeleteAllow());
    }

    public void test_updateAllow() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.updateAllow", "true");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertTrue(config.isUpdateAllow());
    }

    public void test_updateAllow_false() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.updateAllow", "false");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertFalse(config.isUpdateAllow());
    }


    public void test_insertAllow() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.insertAllow", "true");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertTrue(config.isInsertAllow());
    }

    public void test_insertAllow_false() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("druid.wall.insertAllow", "false");
        WallConfig config = new WallConfig();
        config.configFromProperties(properties);

        assertFalse(config.isInsertAllow());
    }
}
