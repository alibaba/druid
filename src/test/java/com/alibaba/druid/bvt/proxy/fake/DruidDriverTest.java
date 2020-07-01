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
package com.alibaba.druid.bvt.proxy.fake;

import java.sql.Connection;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;

public class DruidDriverTest extends TestCase {

    public void test_0() throws Exception {
        String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=preCallTest:jdbc:fake:c1";
        Properties info = new Properties();
        DruidDriver driver = new DruidDriver();
        Connection conn = driver.connect(url, info);
        Assert.assertNotNull(conn);
        Assert.assertEquals("c1", conn.getCatalog());

        conn.setCatalog("c2");
        Assert.assertEquals("c2", conn.getCatalog());

        conn.setTransactionIsolation(100);
        Assert.assertEquals(100, conn.getTransactionIsolation());

        conn.close();
    }
}
