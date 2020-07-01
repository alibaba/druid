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
package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class TestOnlineSQLTest3 extends TestCase {

    private String       url      = "jdbc:mysql://a.b.c.d/dragoon_v25_masterdb";
    private String       user     = "dragoon_test";
    private String       password = "dragoon_test";

    protected Connection conn;

    public void setUp() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, password);
    }

    public void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    public void test_0() throws Exception {
//        ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
//        JdbcUtils.printResultSet(rs);
        
        String sql = "select benchmark( 1, sha1( 'test' ) )";
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        JdbcUtils.printResultSet(rs);
        
        stmt.close();
    }
}
