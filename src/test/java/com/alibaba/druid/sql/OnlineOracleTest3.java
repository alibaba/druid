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
package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class OnlineOracleTest3 extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;
    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@xx.xx.xx.xx:1521:emdb";
        user = "wardon";
        password = "wardon";

        dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaxActive(50);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
//        dataSource.setFilters("wall");
    }

    public void test_connect() throws Exception {
        executeQuery("select UID, USER FROM DUAL");
    }

    public void executeQuery(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            JdbcUtils.printResultSet(rs);

        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public void execute(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();

            stmt = conn.createStatement();
            stmt.execute(sql);

        } finally {
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }
}
