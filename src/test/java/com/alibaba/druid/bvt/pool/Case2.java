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

import java.sql.Connection;
import java.util.Properties;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.JMXUtils;

/**
 * 类Case2.java的实现描述：TODO 类实现描述
 * 
 * @author admin 2011-5-4 下午02:45:21
 */
public class Case2 extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_singleThread() throws Exception {

        Class.forName("com.alibaba.druid.mock.MockDriver");

        Properties properties = new Properties();
        properties.put("maxActive", "100");
        properties.put("maxIdle", "30");
        properties.put("maxWait", "10000");
        properties.put("url", "jdbc:mock:");
        properties.put("filters", "stat");
        properties.put("validationQuery", "SELECT 1");
        DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        JMXUtils.register("com.alibaba.druid:type=DruidDataSource", dataSource);

        final int COUNT = 10;

        Assert.assertEquals(0, dataSource.getCreateCount());
        Assert.assertEquals(0, dataSource.getDestroyCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());

        Connection[] connections = new Connection[COUNT];
        for (int i = 0; i < COUNT; ++i) {
            connections[i] = dataSource.getConnection();
        }

        for (int i = 0; i < COUNT; ++i) {
            connections[i].close();
        }

        Assert.assertEquals(0, dataSource.getDestroyCount());

        dataSource.close();
        Assert.assertEquals(dataSource.getCreateCount(), dataSource.getDestroyCount());
    }
}
