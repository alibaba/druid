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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class TestDruidOraclePreparedStatement extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;

    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
        // jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto"; // error url
        user = "alibaba";
        password = "ccbuauto";

        dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {

        Class.forName(JdbcUtils.getDriverClassName(jdbcUrl));

//        {
//            Connection conn = dataSource.getConnection();
//
//            ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] { "TABLE" });
//            JdbcUtils.printResultSet(metaRs);
//            metaRs.close();
//
//            conn.close();
//        }

//        {
//            Connection conn = dataSource.getConnection();
//            Statement stmt = conn.createStatement();
//
//            ResultSet rs = stmt.executeQuery("SELECT * FROM WP_ORDERS");
//            JdbcUtils.printResultSet(rs);
//            rs.close();
//
//            stmt.close();
//            conn.close();
//        }

        for (int i = 0; i < 3; ++i) {
            Connection conn = dataSource.getConnection();

            // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
            // JdbcUtils.printResultSet(metaRs);
            // metaRs.close();

            String sql = "SELECT * FROM WS_OFFER WHERE ROWNUM <= ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {

            }

            rs.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();

            // ResultSet metaRs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] {"TABLE"});
            // JdbcUtils.printResultSet(metaRs);
            // metaRs.close();

            String sql = "SELECT * FROM WS_OFFER WHERE ROWNUM <= ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 11);
            ResultSet rs = stmt.executeQuery();
            rs.next();

            rs.close();
            stmt.close();
            conn.close();
        }

    }
}
