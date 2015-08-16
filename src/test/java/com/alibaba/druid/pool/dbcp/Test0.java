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
package com.alibaba.druid.pool.dbcp;

import java.sql.Connection;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockDriver;

public class Test0 extends TestCase {

    public void test_idle() throws Exception {
        MockDriver driver = MockDriver.instance;

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setInitialSize(0);
        dataSource.setMaxActive(4);
        dataSource.setMaxIdle(4);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(5000 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Connection conn = dataSource.getConnection();

            // Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            System.out.println("raw size : " + driver.getConnections().size());

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

        dataSource.close();
    }
}
