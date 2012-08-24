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
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HADataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class HADataSourceTest2 extends TestCase {

    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    private HADataSource    dataSourceHA;

    private String          MASTER_URL = "jdbc:mock:master";
    private String          SLAVE_URL  = "jdbc:mock:slave";

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl(MASTER_URL);
        dataSourceA.setFilters("trace");

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl(SLAVE_URL);
        dataSourceB.setFilters("stat");

        dataSourceHA = new HADataSource();
        dataSourceHA.setMaster(dataSourceA);
        dataSourceHA.setSlave(dataSourceB);
    }

    protected void tearDown() throws Exception {
        dataSourceHA.close();

        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }

        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_switch() throws Exception {
        {
            Connection conn = dataSourceHA.getConnection();

            Statement stmt = conn.createStatement();
            stmt.execute("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);

            // Assert.assertEquals(dataSourceA.getUrl(), mockConn.getUrl());

            conn.close();
        }
        dataSourceHA.setMasterEnable(false);
        {
            Connection conn = dataSourceHA.getConnection();

            Statement stmt = conn.createStatement();
            stmt.execute("select 1");

            MockConnection mockConn = conn.unwrap(MockConnection.class);
            Assert.assertNotNull(mockConn);

            // Assert.assertEquals(dataSourceB.getUrl(), mockConn.getUrl());

            conn.close();
        }
    }
}
