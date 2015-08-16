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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TestMySqlPing2 extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;
    private String          driverClass;

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:mysql://a.b.c.d:3308/dragoon_v25_masterdb";
        user = "dragoon_admin";
        password = "dragoon_root";

        driverClass = "com.mysql.jdbc.Driver";

        dataSource  = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        
        dataSource.setMaxActive(4);
        dataSource.setMinIdle(1);
        dataSource.setTestOnBorrow(true);
    }

    public void test_o() throws Exception {
        {
            Connection[] connections = new Connection[3];
            for (int i = 0; i < connections.length; ++i) {
                connections[i] = dataSource.getConnection();
            }
            
            for (int i = 0; i < connections.length; ++i) {
                Statement stmt = connections[i].createStatement();
                ResultSet rs = stmt.executeQuery("select now()");
                JdbcUtils.printResultSet(rs);
                rs.close();
                stmt.close();
            }
            
            for (int i = 0; i < connections.length; ++i) {
                connections[i].close();
            }
        }
        
        


        Thread.sleep(1000 * 60 * 60 * 6); // 6 hours
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select now()");
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();

        conn.close();
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
