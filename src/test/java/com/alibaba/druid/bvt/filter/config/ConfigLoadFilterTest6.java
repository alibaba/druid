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
package com.alibaba.druid.bvt.filter.config;

import java.sql.Connection;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ConfigLoadFilterTest6 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("druid-configFile=https://raw.github.com/AlibabaTech/druid/master/src/test/resources/bvt/config/config-0.properties");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_http_config() throws Exception {
        Assert.assertEquals(true, dataSource.isTestOnBorrow()); // default
        Assert.assertEquals(8, dataSource.getMaxActive());
        
        dataSource.init();

        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(10, dataSource.getMaxActive());
        Assert.assertEquals("jdbc:mock:config-0", dataSource.getUrl());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
