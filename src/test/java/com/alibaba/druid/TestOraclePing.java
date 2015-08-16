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
import oracle.jdbc.OracleConnection;

public class TestOraclePing extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private String SQL;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ointest3";
        user = "alibaba";
        password = "deYcR7facWSJtCuDpm2r";
        SQL = "SELECT * FROM AV_INFO WHERE ID = ?";

        driverClass = "oracle.jdbc.driver.OracleDriver";
    }

    public void test_o() throws Exception {
        Class.forName(driverClass);

        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

        OracleConnection oracleConn = (OracleConnection) conn;

        for (int i = 0; i < 10; ++i) {
            ping_1000(oracleConn);
            select_1000(oracleConn);
        }

        conn.close();
    }

    private void ping_1000(OracleConnection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            pring(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("ping : " + millis);
    }

    private void select_1000(OracleConnection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            select(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("select : " + millis);
    }

    public void pring(OracleConnection oracleConn) throws SQLException {
        oracleConn.pingDatabase(1000);
    }

    public void select(OracleConnection oracleConn) throws SQLException {
        Statement stmt = oracleConn.createStatement();
        stmt.execute("SELECT 1 FROM DUAL");
        stmt.close();
    }
}
