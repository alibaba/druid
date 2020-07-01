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
package com.alibaba.druid.bvt.pool.dynamic;

import java.sql.Connection;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;

public class MaxActiveChangeTest extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(3);
        dataSource.setMinIdle(2);
        dataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 5);
        dataSource.setMaxWait(20);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_maxActive() throws Exception {
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(1, connect(1));
            Assert.assertEquals(1, dataSource.getPoolingCount());
        }
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(2, connect(2));
            Assert.assertEquals(2, dataSource.getPoolingCount());
        }
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(3, connect(3));
            Assert.assertEquals(3, dataSource.getPoolingCount());
        }
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(3, connect(4));
            Assert.assertEquals(3, dataSource.getPoolingCount());
        }

        dataSource.setMaxActive(5);

        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(5, connect(5));
            Assert.assertEquals(5, dataSource.getPoolingCount());
        }

        dataSource.shrink();
        Assert.assertEquals(2, dataSource.getPoolingCount());

        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(5, connect(5));
            Assert.assertEquals(5, dataSource.getPoolingCount());
        }

        Assert.assertEquals(5, dataSource.getPoolingCount());
        dataSource.setMaxActive(3);

        Assert.assertEquals(5, dataSource.getPoolingCount());

        dataSource.shrink();
        Assert.assertEquals(2, dataSource.getPoolingCount());

        // 确保收缩之后不会再长上去
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(3, connect(5));
            Assert.assertEquals(3, dataSource.getPoolingCount());
        }

        dataSource.setMaxActive(2);
        dataSource.shrink();
        Assert.assertEquals(2, dataSource.getPoolingCount());

        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(2, connect(3));
            Assert.assertEquals(2, dataSource.getPoolingCount());
        }

        dataSource.setMinIdle(1);
        dataSource.setMaxActive(1);
        dataSource.shrink();
        Assert.assertEquals(1, dataSource.getPoolingCount());

        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(1, connect(2));
            Assert.assertEquals(1, dataSource.getPoolingCount());
        }

        Exception error = null;
        try {
            dataSource.setMaxActive(0);
        } catch (IllegalArgumentException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSource.getMaxActive());
    }

    public int connect(int count) throws Exception {
        int successCount = 0;
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            try {
                connections[i] = dataSource.getConnection();
                successCount++;
            } catch (GetConnectionTimeoutException e) {
                // skip
            }
        }

        for (int i = 0; i < count; ++i) {
            JdbcUtils.close(connections[i]);
        }

        return successCount;
    }
}
