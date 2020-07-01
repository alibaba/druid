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
package com.alibaba.druid.bvt.pool.basic;

import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidPooledStatement;

public class PoolableStatementTest extends TestCase {

    protected Statement         raw;
    protected DruidPooledStatement stmt;

    protected void setUp() throws Exception {
        raw = new MockStatement(null);
        stmt = new DruidPooledStatement(null, raw) {

            protected SQLException checkException(Throwable error) throws SQLException {
                if (error instanceof SQLException) {
                    return (SQLException) error;
                }

                return new SQLException(error);
            }
        };
    }

    protected void tearDown() throws Exception {

    }

    public void test_basic() throws Exception {
        Assert.assertEquals(raw, stmt.getStatement());
        Assert.assertEquals(null, stmt.getPoolableConnection());
        Assert.assertEquals(null, stmt.getConnection());
        Assert.assertEquals(false, stmt.isPoolable());
        stmt.toString();
    }

}
