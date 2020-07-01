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

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestSharePreparedStatements extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_sharePreparedStatements() throws Exception {

        // sharePreparedStatements

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(30);

        String sql = "SELECT 1";

        MockPreparedStatement mockStmt = null;
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            mockStmt = stmt.unwrap(MockPreparedStatement.class);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }
        
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertEquals(mockStmt, stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertSame(mockStmt, stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();

            conn.close();
        }
        
        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            Assert.assertSame(mockStmt, stmt.unwrap(MockPreparedStatement.class));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            rs.close();
            stmt.close();
            
            conn.close();
        }

        dataSource.close();
    }
}
