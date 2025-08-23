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
package com.alibaba.druid.bvt.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterResultSetMultiCloseTest extends PoolTestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {
        final String sql = "SELECT 1";
        assertTrue(dataSource.isInited());

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        assertNull(sqlStat);

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        assertNotNull(sqlStat);

        assertEquals("first failed", 1, sqlStat.getExecuteAndResultHoldTimeHistogramSum());

        rs.close();

        assertEquals("second failed", 1, sqlStat.getExecuteAndResultHoldTimeHistogramSum());

        stmt.close();

        conn.close();

        assertEquals(1, sqlStat.getExecuteAndResultHoldTimeHistogramSum());
    }
}
