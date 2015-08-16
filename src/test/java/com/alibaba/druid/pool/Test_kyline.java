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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

public class Test_kyline extends TestCase {

    private String url      = "jdbc:mysql://a.b.c.d:8066/amoeba";
    private String user     = "root";
    private String password = "12345";
    private String driver   = "com.mysql.jdbc.Driver";

    public void test_0() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(driver);

        DriverManager.getConnection(url, user, password);

        Connection conn = ds.getConnection();
        conn.close();

        ds.close();
    }
}
