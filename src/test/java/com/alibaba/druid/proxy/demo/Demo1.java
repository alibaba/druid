/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.proxy.demo;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcStatManager;

public class Demo1 extends TestCase {

    public void test_0() throws Exception {
        JdbcStatManager.getInstance().reset(); // 重置计数器

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionStat().getConnectCount());
        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionStat().getCloseCount());

        String url = "jdbc:wrap-jdbc:filters=default:name=preCallTest:jdbc:derby:memory:Demo1;create=true";
        Connection conn = DriverManager.getConnection(url);

        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionStat().getConnectCount());
        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionStat().getCloseCount());

        conn.close();

        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionStat().getConnectCount());
        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionStat().getCloseCount());
    }
}
