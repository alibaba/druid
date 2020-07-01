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
package com.alibaba.druid.pool;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

public class TestMySqlPing extends TestCase {

    public void test_ping() throws Exception {
        String url = "jdbc:mysql://a.b.c.d:3308/dragoon_v25_masterdb";
        String user = "dragoon_admin";
        String password = "dragoon_root";

        Driver driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();

        Connection conn = DriverManager.getConnection(url, user, password);
        ping(conn);
        conn.close();
    }

    public void ping(Connection conn) throws Exception {
        System.out.println(conn.getClass());
        Method method = conn.getClass().getMethod("pring");
        method.invoke(conn);
    }
}
