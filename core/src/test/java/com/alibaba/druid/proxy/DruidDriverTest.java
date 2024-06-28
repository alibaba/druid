package com.alibaba.druid.proxy;

import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class DruidDriverTest {
    @Test
    public void parseConfig() throws Exception {
       // String url = "jdbc:wrap-jdbc:driver=driver1:filters=filters1:name=name1:jmx=true:jdbc:sqlite:";
        Properties properties;
        DataSourceProxyConfig config;
        String url = "";
        //non driver property , getRawDriverClassName() = convert(jdbc:sqlite:) = org.sqlite.JDBC
        url = "jdbc:wrap-jdbc:jdbc:sqlite:";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , JdbcConstants.SQLITE_DRIVER);
        Assert.assertEquals(config.getRawUrl() , "jdbc:sqlite:");

        //have driver property , getRawDriverClassName() = driver1 , getRawUrl()=jdbc:sqlite:
        url = "jdbc:wrap-jdbc:driver=driver1:filters=com.alibaba.druid.filter.stat.StatFilter,com.alibaba.druid.filter.encoding.EncodingConvertFilter:name=name1:jmx=true:jdbc:sqlite:";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , "driver1");
        Assert.assertEquals(config.getName() , "name1");
        Assert.assertEquals(config.isJmxOption() , true);
        Assert.assertEquals(config.getRawUrl() , "jdbc:sqlite:");

        //non driver property , getRawDriverClassName() = convert(jdbc:sqlite:) = org.sqlite.JDBC
        url = "jdbc:wrap-jdbc:name=name2:jmx=true:jdbc:sqlite:";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getName() , "name2");
        Assert.assertEquals(config.getRawDriverClassName() , JdbcConstants.SQLITE_DRIVER);
        Assert.assertEquals(config.getRawUrl() , "jdbc:sqlite:");

        //mix properties order, have driver property , getRawDriverClassName() = driver2
        url = "jdbc:wrap-jdbc:jmx=false:name=name2:filters=com.alibaba.druid.filter.stat.StatFilter:driver=driver2:jdbc:sqlite:";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , "driver2");
        Assert.assertEquals(config.getName() , "name2");
        Assert.assertEquals(config.isJmxOption() , false);
        Assert.assertEquals(config.getRawUrl() , "jdbc:sqlite:");

        //mix properties order, have driver property , getRawDriverClassName() = driver2
        url = "jdbc:wrap-jdbc:jmx=true:name=name2:filters=com.alibaba.druid.filter.stat.StatFilter:driver=driver2:jdbc:sqlite:";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , "driver2");
        Assert.assertEquals(config.getName() , "name2");
        Assert.assertEquals(config.isJmxOption() , true);
        Assert.assertEquals(config.getRawUrl() , "jdbc:sqlite:");

        url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=basicType:jdbc:derby:memory:basicTypeTestDB;create=true";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , "org.apache.derby.jdbc.EmbeddedDriver");
        Assert.assertEquals(config.getName() , "basicType");
        Assert.assertEquals(config.isJmxOption() , false);
        Assert.assertEquals(config.getRawUrl() , "jdbc:derby:memory:basicTypeTestDB;create=true");

        url = "jdbc:wrap-jdbc:jmx=true:filters=default,commonLogging,log4j:name=mydsqlds:jdbc:mysql://127.0.0.1:3306/test?useInformationSchema=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&characterEncoding=utf-8";
        properties = new Properties();
        config = DruidDriver.parseConfig(url,properties);
        Assert.assertEquals(config.getRawDriverClassName() , "com.mysql.cj.jdbc.Driver");
        Assert.assertEquals(config.getName() , "mydsqlds");
        Assert.assertEquals(config.isJmxOption() , true);
        Assert.assertEquals(config.getRawUrl() , "jdbc:mysql://127.0.0.1:3306/test?useInformationSchema=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&characterEncoding=utf-8");



    }
}
