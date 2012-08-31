package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigFileGenerator;
import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.filter.config.loader.impl.FileConfigLoader;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @author Jonas Yang
 */
public class ConfigFilterTest extends ConfigFileGenerator {

    String encryptedString = "OJfUm6WCHi7EuXqE6aEc+Po2xFrAGBeSNy8O2jWhV2FTG8/5kbRRr2rjNKhptlevm/03Y0048P7h88gdUOXAYg==";

    @Test
    public void testInitRemoteConfigFile() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.file=" + FileConfigLoader.PROTOCOL_PREFIX + this.filePath);
        try {
            dataSource.init();

            Assert.assertEquals("The username is " + dataSource.getUsername(), "test1", dataSource.getUsername());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInitRemoteConfigFileBySystemProperty() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("config");

        System.setProperty(ConfigFilter.SYS_PROP_CONFIG_FILE, FileConfigLoader.PROTOCOL_PREFIX + this.filePath);
        try {
            dataSource.init();

            Assert.assertEquals("The username is " + dataSource.getUsername(), "test1", dataSource.getUsername());
        } finally {
            System.clearProperty(ConfigFilter.SYS_PROP_CONFIG_FILE);
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInitInvalidRemoteConfigFile() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.file=abcdef");
        try {
            dataSource.init();
            Assert.assertTrue("It is here", false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInitDecrypt() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriver(MockDriver.instance);
        dataSource.setUrl("");
        dataSource.setUsername("test");
        dataSource.setPassword(encryptedString);
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.decrypt=RSA");

        try {
            dataSource.init();
            Assert.assertEquals("The password is " + dataSource.getPassword() + ", is not xiaoyu", "xiaoyu", dataSource.getPassword());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInitInvalidDecrypt() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriver(MockDriver.instance);
        dataSource.setUrl("");
        dataSource.setUsername("test");
        dataSource.setPassword(encryptedString);
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.decrypt=ABCDEF");

        try {
            dataSource.init();
            Assert.assertTrue("It is here", false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInitRemoteConfigAndDecrypt() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.decrypt=RSA;config.file=" + FileConfigLoader.PROTOCOL_PREFIX + this.filePath);
        try {
            dataSource.init();

            Assert.assertEquals("The password is " + dataSource.getPassword(), "xiaoyu", dataSource.getPassword());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testNormalInit() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@");

        try {
            dataSource.init();
        } finally {
            JdbcUtils.close(dataSource);
        }
    }

    @Test
    public void testInvalidInit() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriver(MockDriver.instance);
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.file=abcdefeg");

        try {
            dataSource.init();
            Assert.assertTrue("Get here.", false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.close(dataSource);
        }
    }
}
