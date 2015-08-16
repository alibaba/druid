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
package com.alibaba.druid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;


public class TestMySqlPing extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:mysql://a.b.c.d:3308/dragoon_v25_masterdb";
        user = "dragoon_admin";
        password = "dragoon_root";

        driverClass = "com.mysql.jdbc.Driver";
    }

    public void test_o() throws Exception {
        Class.forName(driverClass);

        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

        com.mysql.jdbc.Connection mysqlConn = (com.mysql.jdbc.Connection) conn;

        for (int i = 0; i < 10; ++i) {
            ping_1000(mysqlConn);
            select_1000(mysqlConn);
        }

        conn.close();
    }

    private void ping_1000(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            pring(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("ping : " + millis);
    }
    
    private void select_1000(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            select(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("select : " + millis);
    }

    public void pring(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        oracleConn.ping();
    }

    public void select(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        Statement stmt = oracleConn.createStatement();
        stmt.execute("SELECT 'x'");
        stmt.close();
    }
}
