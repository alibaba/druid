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
package com.alibaba.druid.bvt.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.DruidNativeJdbcExtractor;

public class DruidJdbcExtractorTest extends TestCase {

    public void test_spring() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        try {
            DruidNativeJdbcExtractor extractor = new DruidNativeJdbcExtractor();

            dataSource.setUrl("jdbc:mock:xx1");
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(true, extractor.getNativeConnection(conn) instanceof MockConnection);
            
            Statement stmt = conn.createStatement();
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(stmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(stmt) instanceof MockStatement);
            
            stmt.close();
            
            PreparedStatement preStmt = conn.prepareStatement("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(preStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(preStmt) instanceof MockPreparedStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(preStmt) instanceof MockPreparedStatement);
            preStmt.close();
            
            PreparedStatement callStmt = conn.prepareCall("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(callStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(callStmt) instanceof MockCallableStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(callStmt) instanceof MockCallableStatement);
            callStmt.close();
            
            conn.close();
        } finally {
            dataSource.close();
        }
    }

    public void test_spring_filter() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        try {
            DruidNativeJdbcExtractor extractor = new DruidNativeJdbcExtractor();

            dataSource.setUrl("jdbc:mock:xx1");
            dataSource.setFilters("stat");
            Connection conn = dataSource.getConnection();
            Assert.assertEquals(true, extractor.getNativeConnection(conn) instanceof MockConnection);
            
            Statement stmt = conn.createStatement();
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(stmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(stmt) instanceof MockStatement);
            
            stmt.close();
            
            PreparedStatement preStmt = conn.prepareStatement("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(preStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(preStmt) instanceof MockPreparedStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(preStmt) instanceof MockPreparedStatement);
            preStmt.close();
            
            PreparedStatement callStmt = conn.prepareCall("select 1");
            Assert.assertEquals(true, extractor.getNativeConnectionFromStatement(callStmt) instanceof MockConnection);
            Assert.assertEquals(true, extractor.getNativeStatement(callStmt) instanceof MockCallableStatement);
            Assert.assertEquals(true, extractor.getNativePreparedStatement(callStmt) instanceof MockCallableStatement);
            callStmt.close();
            
            conn.close();
        } finally {
            dataSource.close();
        }
    }
}
