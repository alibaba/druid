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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;

public class CallableStatmentTest extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setFilters("log4j");

        dataSource.setDriver(new MockDriver() {

            public MockCallableStatement createMockCallableStatement(MockConnection conn, String sql) {
                return new MyMockCallableStatement(conn, sql);
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_connect() throws Exception {
        MockCallableStatement rawStmt = null;
        MockResultSet rawRs = null;
        {
            Connection conn = dataSource.getConnection();

            CallableStatement stmt = conn.prepareCall("select 1");
            stmt.execute();
            rawStmt = stmt.unwrap(MockCallableStatement.class);

            ResultSet rs = (ResultSet) stmt.getObject(0);
            
            rawRs = rs.unwrap(MockResultSet.class);
            
            rs.next();
            
            rs.close();
            stmt.close();
            
            Assert.assertFalse(rawStmt.isClosed());
            Assert.assertTrue(rawRs.isClosed());
            
            rawRs = rs.unwrap(MockResultSet.class);
            Assert.assertNotNull(rawRs);

            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();

            CallableStatement stmt = conn.prepareCall("select 1");
            stmt.execute();

            Assert.assertSame(rawStmt, stmt.unwrap(MockCallableStatement.class));
            Assert.assertFalse(rawStmt.isClosed());

            stmt.getObject(0);

            ResultSet rs = (ResultSet) stmt.getObject(0);
            rs.next();
            rs.close();

            stmt.close();

            conn.close();
        }
    }

    public static class MyMockCallableStatement extends MockCallableStatement {

        public MyMockCallableStatement(MockConnection conn, String sql){
            super(conn, sql);
        }

        public Object getObject(int index) throws SQLException {
            return this.getConnection().getDriver().createResultSet(this);
        }
    }
}
