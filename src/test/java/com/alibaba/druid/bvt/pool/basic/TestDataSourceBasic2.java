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
package com.alibaba.druid.bvt.pool.basic;

import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDataSourceBasic2 extends TestCase {

    public void test_0() throws Exception {
        DruidDataSourceStatManager.clear();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        dataSource.setBreakAfterAcquireFailure(true);
        Assert.assertEquals(true, dataSource.isBreakAfterAcquireFailure());

        dataSource.setConnectionErrorRetryAttempts(234);
        Assert.assertEquals(234, dataSource.getConnectionErrorRetryAttempts());

        dataSource.setMaxPoolPreparedStatementPerConnectionSize(234);
        Assert.assertEquals(234, dataSource.getMaxPoolPreparedStatementPerConnectionSize());

        dataSource.incrementDupCloseCount();
        Assert.assertEquals(1, dataSource.getDupCloseCount());

        dataSource.setValidConnectionChecker(null);
        dataSource.setValidConnectionCheckerClassName(null);
        Assert.assertEquals(null, dataSource.getValidConnectionChecker());

        dataSource.addConnectionProperty("user", "ljw");
        Assert.assertEquals(1, dataSource.getConnectProperties().size());

        Assert.assertEquals(0, dataSource.getConnectionInitSqls().size());
        dataSource.setConnectionInitSqls(Arrays.<Object> asList("SELECT 1", null, ""));
        Assert.assertEquals(1, dataSource.getConnectionInitSqls().size());

        Assert.assertEquals(30 * 1000, dataSource.getTimeBetweenConnectErrorMillis());
        Assert.assertEquals(234, dataSource.getMaxOpenPreparedStatements());
        Assert.assertEquals(300, dataSource.getRemoveAbandonedTimeout());
        dataSource.setRemoveAbandonedTimeout(400);
        Assert.assertEquals(400, dataSource.getRemoveAbandonedTimeout());
        Assert.assertEquals(400 * 1000, dataSource.getRemoveAbandonedTimeoutMillis());
        Assert.assertEquals(3, dataSource.getNumTestsPerEvictionRun());
        dataSource.setNumTestsPerEvictionRun(4);
        Assert.assertEquals(4, dataSource.getNumTestsPerEvictionRun());

        dataSource.setMaxWaitThreadCount(4);
        Assert.assertEquals(4, dataSource.getMaxWaitThreadCount());

        dataSource.setValidationQueryTimeout(4);
        Assert.assertEquals(4, dataSource.getValidationQueryTimeout());

        dataSource.setAccessToUnderlyingConnectionAllowed(true);
        Assert.assertEquals(true, dataSource.isAccessToUnderlyingConnectionAllowed());

        dataSource.setDefaultReadOnly(true);
        Assert.assertEquals(Boolean.TRUE, dataSource.getDefaultReadOnly());

        dataSource.setDefaultTransactionIsolation(10);
        Assert.assertEquals(Integer.valueOf(10), dataSource.getDefaultTransactionIsolation());

        dataSource.setDefaultCatalog("xxx");
        Assert.assertEquals("xxx", dataSource.getDefaultCatalog());

        dataSource.setPasswordCallbackClassName(null);
        dataSource.setUserCallback(null);

        Assert.assertEquals(0, dataSource.getQueryTimeout());
        dataSource.setQueryTimeout(10001);
        Assert.assertEquals(10001, dataSource.getQueryTimeout());

        Assert.assertEquals(-1, dataSource.getMaxWait());
        dataSource.setMaxWait(10001);
        Assert.assertEquals(10001, dataSource.getMaxWait());

        Assert.assertEquals(8, dataSource.getMaxIdle());
        dataSource.setMaxIdle(3);
        Assert.assertEquals(3, dataSource.getMaxIdle());

        Assert.assertEquals(0, dataSource.getLoginTimeout());
        dataSource.setLoginTimeout(30);
        Assert.assertEquals(30, dataSource.getLoginTimeout());

        Assert.assertEquals(null, dataSource.getUsername());
        dataSource.setUsername("ljw");
        Assert.assertEquals("ljw", dataSource.getUsername());

        Assert.assertEquals(null, dataSource.getPassword());
        dataSource.setPassword("xxx");
        Assert.assertEquals("xxx", dataSource.getPassword());

        dataSource.setConnectProperties(new Properties());
        Assert.assertEquals(0, dataSource.getConnectProperties().size());
        dataSource.setConnectionProperties("a=1;b=2;c");
        Assert.assertEquals(3, dataSource.getConnectProperties().size());

        dataSource.setExceptionSorter((ExceptionSorter) null);

        dataSource.close();
    }

    protected void tearDown() throws Exception {
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }
    }

}
