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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import com.alibaba.druid.filter.mysql8datetime.MySQL8DateTimeSqlTypeFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;
import java.sql.Timestamp;
import org.junit.Assert;

/**
 * lizongbo
 */
public class MySQL8DateTimeSqlTypeFilterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("mysql8DateTime");

        dataSource.setDriver(new MockDriver() {
            public ResultSet createResultSet(MockPreparedStatement stmt) {
                return new MyResultSet(stmt);
            }

            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                return new MyResultSet(stmt);
            }
        });

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_mysql8datetime() throws Exception {
        Assert.assertTrue(dataSource.isInited());

        MySQL8DateTimeSqlTypeFilter filter = (MySQL8DateTimeSqlTypeFilter) dataSource.getProxyFilters().get(0);

        DruidPooledConnection conn = dataSource.getConnection();

        final String PARAM_VALUE = "中国";
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setString(1, PARAM_VALUE);

        ResultSet rs = stmt.executeQuery();
        MyResultSet rawRs = rs.unwrap(MyResultSet.class);

        rs.next();
        Object obj1 = rs.getObject(1);
        System.out.println(obj1.getClass() + "|" + obj1);
        Assert.assertEquals(Timestamp.class, obj1.getClass());
        Object obj2 = rs.getObject("cc");
        System.out.println(obj2.getClass() + "|" + obj2);
        Assert.assertEquals(Timestamp.class, obj2.getClass());

        rs.close();
        stmt.close();

        conn.close();

    }

    public static class MyResultSet extends MockResultSet {

        public MyResultSet(Statement statement) {
            super(statement);
        }

        @Override
        public Object getObject(int index) throws SQLException {
            return LocalDateTime.now();
        }

        @Override
        public Object getObject(String columnLabel) throws SQLException {
            return LocalDateTime.now();
        }


    }
}
