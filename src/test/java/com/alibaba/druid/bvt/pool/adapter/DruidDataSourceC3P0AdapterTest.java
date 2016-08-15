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
package com.alibaba.druid.bvt.pool.adapter;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class DruidDataSourceC3P0AdapterTest extends TestCase {

    private MockDriver                 driver;
    private DruidDataSourceC3P0Adapter dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver();

        dataSource = new DruidDataSourceC3P0Adapter();
        dataSource.setJdbcUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialPoolSize(1);
        dataSource.setMaxPoolSize(2);
        dataSource.setMinPoolSize(1);
        dataSource.setMaxIdleTime(300); // 300 / 10
        dataSource.setIdleConnectionTestPeriod(180); // 180 / 10
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setPreferredTestQuery("SELECT 1");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_basic() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
