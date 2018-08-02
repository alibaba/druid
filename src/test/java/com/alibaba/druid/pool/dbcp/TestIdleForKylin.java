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
package com.alibaba.druid.pool.dbcp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdleForKylin extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = MockDriver.instance;

        // BasicDataSource dataSource = new BasicDataSource();
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(10);
        dataSource.setMinIdle(0);
        dataSource.setMinEvictableIdleTimeMillis(50000 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(500);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();

            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }

        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }

        {
            int count = 4;
            Connection[] connections = new Connection[4];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
            }
            System.out.println("raw size : " + driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
            }
            System.out.println("raw size : " + driver.getConnections().size());

            System.out.println("----------sleep for evict");
            Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
            System.out.println("raw size : " + driver.getConnections().size());
        }

        System.out.println("----------raw close all connection");
        for (MockConnection rawConn : driver.getConnections()) {
            rawConn.close();
        }

        Thread.sleep(dataSource.getMinEvictableIdleTimeMillis() * 2);
        System.out.println("raw size : " + driver.getConnections().size());
        {
            Connection conn = dataSource.getConnection();
            System.out.println("raw size : " + driver.getConnections().size());
            conn.close();
            System.out.println("raw size : " + driver.getConnections().size());
        }

        dataSource.close();
    }
}
