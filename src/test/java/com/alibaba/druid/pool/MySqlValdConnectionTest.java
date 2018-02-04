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

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;

public class MySqlValdConnectionTest extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:mysql://a.b.c.d/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.mysql.jdbc.Driver";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setExceptionSorter(MySqlExceptionSorter.class.getName());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
