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

import java.lang.reflect.Field;
import java.sql.Connection;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;

public class ConnectPropertiesChangeTest1 extends PoolTestCase {

    private DruidDataSource dataSource;

    private Log             dataSourceLog;

    protected void setUp() throws Exception {
        super.setUp();

        Field logField = DruidDataSource.class.getDeclaredField("LOG");
        logField.setAccessible(true);
        dataSourceLog = (Log) logField.get(null);

        dataSourceLog.resetStat();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setConnectionProperties("druid.filters=stat;druid.stat.sql.MaxSize=234");
        dataSource.init();

        Assert.assertEquals(1, dataSourceLog.getInfoCount());
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        super.tearDown();
    }

    public void test_connectPropertiesChange() throws Exception {
        Assert.assertEquals(2, dataSource.getConnectProperties().size());

        Connection conn = dataSource.getConnection();
        conn.close();

        StatFilter filter = dataSource.unwrap(StatFilter.class);
        Assert.assertNotNull(filter);
        Assert.assertFalse(filter.isMergeSql());
        Assert.assertEquals(234, dataSource.getDataSourceStat().getMaxSqlSize());

        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.sql.MaxSize=456");
        Assert.assertEquals(456, dataSource.getDataSourceStat().getMaxSqlSize());
        
        Assert.assertTrue(filter.isMergeSql());

        Assert.assertEquals(2, dataSource.getConnectProperties().size());

        Assert.assertEquals("true", dataSource.getConnectProperties().getProperty("druid.stat.mergeSql"));

        dataSource.setConnectionProperties("druid.stat.mergeSql=false");

        Assert.assertFalse(filter.isMergeSql());

        Assert.assertEquals("false", dataSource.getConnectProperties().getProperty("druid.stat.mergeSql"));
    }
}
