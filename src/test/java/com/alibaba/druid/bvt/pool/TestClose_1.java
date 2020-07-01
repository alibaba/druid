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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestClose_1 extends PoolTestCase {

    public void test_close() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUrl("jdbc:mock:xxx");

        String sql = "SELECT 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertEquals(false, rs.isClosed());

        Assert.assertEquals(false, stmt.isClosed());
        stmt.close();
        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        PreparedStatement stmt2 = conn.prepareStatement(sql);
        Assert.assertEquals(true, stmt.isClosed());

        Assert.assertEquals(stmt.unwrap(MockStatement.class), stmt2.unwrap(MockStatement.class));

        conn.close();

        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        dataSource.close();
    }
}
