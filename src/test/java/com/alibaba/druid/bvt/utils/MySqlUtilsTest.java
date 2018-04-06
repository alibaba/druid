package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.util.MySqlUtils;
import com.mysql.jdbc.Driver;
import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.sql.Connection;

public class MySqlUtilsTest extends TestCase {
    public void test_xa() throws Exception {
        Driver driver = new Driver();
        int majorVersion = driver.getMajorVersion();

        if (majorVersion == 5) {
            Class<?> clazz_ConnectionImpl = Class.forName("com.mysql.jdbc.ConnectionImpl");
            Constructor<?> constructor = clazz_ConnectionImpl.getDeclaredConstructor();
            constructor.setAccessible(true);
            Connection conn = (Connection) constructor.newInstance();
            MySqlUtils.createXAConnection(driver, conn);
        } else if (majorVersion == 6) {
            Class<?> clazz_ConnectionImpl = Class.forName("com.mysql.cj.jdbc.ConnectionImpl");
            Constructor<?> constructor = clazz_ConnectionImpl.getDeclaredConstructor();
            constructor.setAccessible(true);
            Connection conn = (Connection) constructor.newInstance();
            try {
                MySqlUtils.createXAConnection(driver, conn);
            } catch (NullPointerException ex) {
                //skip
            }
        }
    }
}
