package com.alibaba.druid.test.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;

import org.junit.Assert;

import com.alibaba.druid.util.OracleUtils;

public class OracleUtilsTest extends TestCase {
    public void test_oracle() throws Exception {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("pingDatabase")) {
                    return 1;
                }
                
                return null;
            }
            
        };
        OracleConnection conn = (OracleConnection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] {OracleConnection.class}, handler);
        
        Assert.assertNotNull(OracleUtils.unwrap(conn));
        
        Assert.assertEquals(1, OracleUtils.pingDatabase(conn));
    }
}
