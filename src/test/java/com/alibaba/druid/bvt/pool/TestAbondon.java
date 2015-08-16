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
package com.alibaba.druid.bvt.pool;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestAbondon extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        dataSource = new DruidDataSource();
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(10);
        dataSource.setLogAbandoned(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000);
        dataSource.setUrl("jdbc:mock:xxx");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        Assert.assertEquals(false, conn.isClosed());
        Thread.sleep(10);
        
        for (int i = 0; i < 100; ++i) {
            if (conn.isAbandonded()) {
                break;
            }
            Thread.sleep(10);    
        }
        Assert.assertEquals(true, conn.isAbandonded());
    }
}
