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
package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterExecuteTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        MockDriver driver = new MockDriver() {

            public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
                return new MyMockPreparedStatement(conn, sql);
            }
        };

        dataSource.setDriver(driver);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {

        Assert.assertTrue(dataSource.isInited());
        final String sql = "update x";
        
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        
        Assert.assertEquals(0, dataSource.getDataSourceStat().getSqlStat(sql).getExecuteAndResultHoldTimeHistogramSum());
        
        boolean firstResult = stmt.execute();
        Assert.assertFalse(firstResult);

        stmt.close();

        conn.close();

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(1, sqlStat.getHistogramSum());

        Assert.assertEquals(1, sqlStat.getExecuteAndResultHoldTimeHistogramSum());
    }

    static class MyMockPreparedStatement extends MockPreparedStatement {

        public MyMockPreparedStatement(MockConnection conn, String sql){
            super(conn, sql);
        }

        public boolean execute() throws SQLException {
            return false;
        }

        public int getUpdateCount() throws SQLException {
            return 100;
        }
    }
}
