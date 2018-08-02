/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
