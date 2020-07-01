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
package com.alibaba.druid.bvt.bug;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_happyday517_3 extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    private int originalDataSourceCount = 0;
    
    protected void setUp() throws Exception {
        originalDataSourceCount = DruidDataSourceStatManager.getInstance().getDataSourceList().size();
        
        driver = new MockDriver();
        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,trace,log4j,encoding");
        dataSource.setDefaultAutoCommit(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(originalDataSourceCount, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_bug() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(false, conn.getAutoCommit());
        conn.close();
    }
}
