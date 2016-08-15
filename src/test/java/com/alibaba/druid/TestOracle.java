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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class TestOracle extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private String SQL;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:xx";
        user = "a";
        password = "b";
        SQL = "SELECT * FROM AV_INFO WHERE ID = ?";

        driverClass = "oracle.jdbc.driver.OracleDriver";
    }

    public void test_o() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(50);
        dataSource.setUseOracleImplicitCache(true);
        // dataSource.setConnectionProperties("oracle.jdbc.FreeMemoryOnEnterImplicitCache=true");

        for (int i = 1; i <= 1; ++i) {
            Connection conn = dataSource.getConnection();

            int rowNum = i + 1; // (i % 50) + 1;
            String sql = SQL + " AND ROWNUM <= " + rowNum;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 61);
            ResultSet rs = stmt.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            Assert.assertEquals(true, rowCount > 0);
            // Assert.isTrue(!rs.isClosed());
            rs.close();
            // Assert.isTrue(!stmt.isClosed());
            stmt.close();

            conn.close();

        }

        dataSource.close();
    }
}
