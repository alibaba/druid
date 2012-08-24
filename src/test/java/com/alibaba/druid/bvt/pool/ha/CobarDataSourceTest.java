/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.cobar.CobarConfigLoader;
import com.alibaba.druid.pool.ha.cobar.CobarDataSource;
import com.alibaba.druid.pool.ha.cobar.CobarFailureDetecter;
import com.alibaba.druid.support.json.JSONUtils;

public class CobarDataSourceTest extends TestCase {

    public void test_cobarDataSource() throws Exception {
        String url = "jdbc:cobar://127.0.0.1:8080/druid";

        final HashMap<String, Object> config = new HashMap<String, Object>();

        ArrayList<Object> cobarList = new ArrayList<Object>();

        HashMap<String, Object> cobarA = new HashMap<String, Object>();
        cobarA.put("ip", "mock");
        cobarA.put("port", 80);
        cobarA.put("schema", "cobarA");
        cobarA.put("weight", 1);

        cobarList.add(cobarA);

        HashMap<String, Object> cobarB = new HashMap<String, Object>();
        cobarB.put("ip", "mock");
        cobarB.put("port", 81);
        cobarB.put("schema", "cobarB");
        cobarB.put("weight", 1);

        cobarList.add(cobarB);

        final String mockUrlA = "jdbc:mock://mock:80/cobarA";
        final String mockUrlB = "jdbc:mock://mock:81/cobarB";
        
        final AtomicBoolean statusA = new AtomicBoolean(true);
        final AtomicBoolean statusB = new AtomicBoolean(true);

        config.put("cobarList", cobarList);

        final CobarDataSource dataSource = new CobarDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername("test");
        dataSource.setPassword("");
        dataSource.setMaxWait(1);
        dataSource.setConfigLoadPeriodMillis(10);
        dataSource.setFailureDetectPeriodMillis(1);

        CobarConfigLoader configLoader = new CobarConfigLoader(dataSource) {

            public void load() throws SQLException {
                responseMessage = JSONUtils.toJSONString(config);

                handleResponseMessage();
            }

            protected String createJdbcUrl(String ip, int port, String schema) {
                return "jdbc:mock://" + ip + ":" + port + "/" + schema;
            }
        };
        dataSource.setConfigLoader(configLoader);
        
        CobarFailureDetecter failureDetector = new CobarFailureDetecter() {
            public boolean isValidConnection(DruidDataSource dataSource, Connection conn) {
                MockConnection mockConn;
                try {
                    mockConn = conn.unwrap(MockConnection.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                if (mockConn.getUrl().equals(mockUrlA)) {
                    return statusA.get();
                }
                return statusB.get();
            }
        };
        dataSource.setFailureDetector(failureDetector);

        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");
            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }

        cobarList.remove(cobarB);
        Assert.assertEquals(1, cobarList.size());
        Thread.sleep(dataSource.getConfigLoadPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlA, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        cobarList.add(cobarB);
        Assert.assertEquals(2, cobarList.size());
        cobarList.remove(cobarA);
        Assert.assertEquals(1, cobarList.size());
        Thread.sleep(dataSource.getConfigLoadPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlB, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        cobarList.add(cobarA);
        Assert.assertEquals(2, cobarList.size());
        Thread.sleep(dataSource.getConfigLoadPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        statusA.set(false);
        Thread.sleep(dataSource.getFailureDetectPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlB, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        statusA.set(true);
        statusB.set(false);
        Thread.sleep(dataSource.getFailureDetectPeriodMillis() * 2);
        for (int i = 0; i < 100; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);
            Assert.assertEquals(mockUrlA, mockConn.getUrl());

            rs.next();
            rs.close();
            stmt.close();
            conn.close();
        }
        
        dataSource.close();
    }

}
