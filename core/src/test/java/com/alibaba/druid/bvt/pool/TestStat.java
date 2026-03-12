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
package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class TestStat {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setName("com.alibaba.dragoon.monitor");
        dataSource.setMinIdle(0);
        dataSource.setPoolPreparedStatements(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    @Test
    public void test_stat() throws Exception {
        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        conn.close();

        assertEquals(true, stmt.isClosed());
        assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        dataSource.shrink();

        JdbcStatManager.getInstance().getDataSourceList();
        assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }
}
