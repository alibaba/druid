package com.alibaba.druid.bvt.bug;

import java.sql.Driver;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class Bug_for_JeffYin extends TestCase {

    public void test_0() throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;";
        String driverClassName = JdbcUtils.getDriverClassName(url);
        Class<?> driverClass = JdbcUtils.loadDriverClass(driverClassName);
        Assert.assertNotNull(driverClass);
        
        Driver driver = (Driver) driverClass.newInstance();
        
        Assert.assertTrue(driver.acceptsURL(url));
    }
}
