/*
 * Copyright 2011 Alibaba Group.
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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;

public class TestDruidDataSource extends TestCase {

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        Class.forName("com.alibaba.druid.mock.MockDriver");

        dataSource.setInitialSize(10);
        dataSource.setMaxIdle(10);
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:mock:xxx");

        Assert.assertEquals(0, dataSource.getConnectCount());
        Assert.assertEquals(0, dataSource.getConnectErrorCount());
        Assert.assertEquals(0, dataSource.getCloseCount());
        Assert.assertEquals(0, dataSource.getPoolingSize());

        Connection conn = dataSource.getConnection();

        Assert.assertTrue(conn instanceof PoolableConnection);

        Assert.assertEquals(1, dataSource.getConnectCount());
        Assert.assertEquals(0, dataSource.getConnectErrorCount());
        Assert.assertEquals(0, dataSource.getCloseCount());
        Assert.assertEquals(0, dataSource.getRecycleCount());
        Assert.assertEquals(1, dataSource.getActiveCount());
        Assert.assertEquals(9, dataSource.getPoolingSize());

        conn.close();

        Assert.assertEquals(1, dataSource.getConnectCount());
        Assert.assertEquals(0, dataSource.getConnectErrorCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(10, dataSource.getPoolingSize());

        conn.close(); // 重复close

        Assert.assertEquals(1, dataSource.getConnectCount());
        Assert.assertEquals(0, dataSource.getConnectErrorCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Assert.assertEquals(1, dataSource.getRecycleCount());
        Assert.assertEquals(0, dataSource.getActiveCount());

        dataSource.close();
    }
}
