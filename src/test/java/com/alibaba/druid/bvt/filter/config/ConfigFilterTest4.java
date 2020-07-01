package com.alibaba.druid.bvt.filter.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

public class ConfigFilterTest4 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setFilters("config");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        String password = "abcdefg1234";
        String[] keys = ConfigTools.genKeyPair(1024);

        File file = File.createTempFile("MyTest", Long.toString(System.nanoTime()));
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xx0");
        properties.put(DruidDataSourceFactory.PROP_USERNAME, "sa");
        properties.put(DruidDataSourceFactory.PROP_PASSWORD, ConfigTools.encrypt(keys[0], password));
        properties.put(ConfigFilter.CONFIG_DECRYPT, "true");
        properties.store(new FileOutputStream(file), "");

        dataSource.getConnectProperties().put(ConfigFilter.CONFIG_KEY, keys[1]);
        dataSource.getConnectProperties().put(ConfigFilter.CONFIG_FILE, "file://" + file.getAbsolutePath());

        dataSource.init();

        Assert.assertEquals("jdbc:mock:xx0", dataSource.getUrl());
        Assert.assertEquals("sa", dataSource.getUsername());
        Assert.assertEquals(password, dataSource.getPassword());
    }

    public void test_sys_property() throws Exception {
        String password = "abcdefg1234";
        String[] keys = ConfigTools.genKeyPair(1024);

        File file = File.createTempFile("MyTest", Long.toString(System.nanoTime()));
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_URL, "jdbc:mock:xx0");
        properties.put(DruidDataSourceFactory.PROP_USERNAME, "sa");
        properties.put(DruidDataSourceFactory.PROP_PASSWORD, ConfigTools.encrypt(keys[0], password));
        properties.put(ConfigFilter.CONFIG_DECRYPT, "true");
        properties.store(new FileOutputStream(file), "");

        System.getProperties().put(ConfigFilter.SYS_PROP_CONFIG_KEY, keys[1]);
        System.getProperties().put(ConfigFilter.SYS_PROP_CONFIG_FILE, "file://" + file.getAbsolutePath());

        try {
            dataSource.init();

            Assert.assertEquals("jdbc:mock:xx0", dataSource.getUrl());
            Assert.assertEquals("sa", dataSource.getUsername());
            Assert.assertEquals(password, dataSource.getPassword());
        } finally {
            System.clearProperty(ConfigFilter.SYS_PROP_CONFIG_KEY);
            System.clearProperty(ConfigFilter.SYS_PROP_CONFIG_FILE);
        }
    }
}
