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
package com.alibaba.druid.pool;

import java.sql.CallableStatement;
import java.sql.Connection;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockDriver;

public class DBCPTest extends TestCase {

    public void test_dbcp() throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(MockDriver.class.getName());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setPoolPreparedStatements(true);

        final String sql = "selelct 1";
        {
            Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql);
            stmt.close();
            conn.close();
        }
    }
}
