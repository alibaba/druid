package com.alibaba.druid.bvt.utils;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.util.JdbcUtils;


public class JdbcUtilsTest1 extends TestCase {
    public void test_jdbc() throws Exception {
        Assert.assertTrue(JdbcUtils.createDriver(MockDriver.class.getName()) instanceof MockDriver);
    }
    
    public void test_jdbc_1() throws Exception {
        class MyClassLoader extends ClassLoader {
            
        };
        
        MyClassLoader classLoader = new MyClassLoader();
        Assert.assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);
    }
    
    public void test_jdbc_2() throws Exception {
        class MyClassLoader extends ClassLoader {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return null;
            }
        };
        
        MyClassLoader classLoader = new MyClassLoader();
        
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        
        Assert.assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);
        
        Thread.currentThread().setContextClassLoader(contextLoader);
    }
    
    public void test_jdbc_3() throws Exception {
        class MyClassLoader extends ClassLoader {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return null;
            }
        };
        
        MyClassLoader classLoader = new MyClassLoader();
        
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        Assert.assertTrue(JdbcUtils.createDriver(classLoader, MockDriver.class.getName()) instanceof MockDriver);
        
        Thread.currentThread().setContextClassLoader(contextLoader);
    }
}
