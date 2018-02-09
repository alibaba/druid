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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestLogLongTimeTransaction extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    protected void setUp() throws Exception {
        driver = new MockDriver() {

            protected ResultSet executeQuery(MockStatement stmt, String sql) throws SQLException {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return super.executeQuery(stmt, sql);
            }
        };

        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,trace,log4j,encoding");
        dataSource.setTransactionThresholdMillis(1);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();

        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.next();
        rs.close();
        stmt.close();
        conn.commit();
        conn.close();
    }
}
