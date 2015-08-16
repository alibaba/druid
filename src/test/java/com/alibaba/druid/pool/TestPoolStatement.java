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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class TestPoolStatement extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@a.b.c.d:1521:testconn");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername("alibaba");
        dataSource.setPassword("alibaba");
    }

    public void test_0() throws Exception {
        stat();
        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 5; ++i) {
            exec();
        }
        long millis = System.currentTimeMillis() - startMillis;
        stat();
        System.out.println();
        System.out.println("millis : " + millis);
    }

    private void stat() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "SELECT sysdate,name,value FROM V$SYSSTAT WHERE NAME IN ('parse count (total)', 'execute count')";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        JdbcUtils.printResultSet(rs);
        rs.close();
        stmt.close();
        conn.close();
    }

    private void exec() throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "select * from ALIBABA.ORDER_MAIN WHERE ID = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, new Random().nextInt(1000 * 100));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
