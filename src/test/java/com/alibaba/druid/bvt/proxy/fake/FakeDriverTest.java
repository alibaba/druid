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
package com.alibaba.druid.bvt.proxy.fake;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;

public class FakeDriverTest extends TestCase {

    public void test_0() throws Exception {
        String url = "jdbc:fake:x1";
        Properties info = new Properties();

        String sql = "SELECT 1";

        MockDriver driver = new MockDriver();

        Connection conn = driver.connect(url, info);
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        Assert.assertEquals(true, rs.next());
        Assert.assertEquals(1, rs.getInt(1));

        conn.close();
    }
}
