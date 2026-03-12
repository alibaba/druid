package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUtilsTest1 {
    @Test
    public void test_jdbc() throws Exception {
        assertTrue(JdbcUtils.createDriver(MockDriver.class.getName()) instanceof MockDriver);
    }

    @Test
    public void test_jdbc_1() throws Exception {
        class MyClassLoader extends ClassLoader {
        }

        MyClassLoader classLoader = new MyClassLoader();
        assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);
    }

    @Test
    public void test_jdbc_2() throws Exception {
        class MyClassLoader extends ClassLoader {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return null;
            }
        }

        MyClassLoader classLoader = new MyClassLoader();

        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);

        Thread.currentThread().setContextClassLoader(contextLoader);
    }

    @Test
    public void test_jdbc_3() throws Exception {
        class MyClassLoader extends ClassLoader {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return null;
            }
        }

        MyClassLoader classLoader = new MyClassLoader();

        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);

        assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);

        Thread.currentThread().setContextClassLoader(contextLoader);
    }
}
