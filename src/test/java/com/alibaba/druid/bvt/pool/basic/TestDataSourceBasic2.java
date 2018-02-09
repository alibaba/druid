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
package com.alibaba.druid.bvt.pool.basic;

import java.util.Arrays;
import java.util.Properties;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDataSourceBasic2 extends PoolTestCase {

    public void test_0() throws Exception {
        DruidDataSourceStatManager.clear();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");

        dataSource.setBreakAfterAcquireFailure(true);
        assertEquals(true, dataSource.isBreakAfterAcquireFailure());

        dataSource.setConnectionErrorRetryAttempts(234);
        assertEquals(234, dataSource.getConnectionErrorRetryAttempts());

        dataSource.setMaxPoolPreparedStatementPerConnectionSize(234);
        assertEquals(234, dataSource.getMaxPoolPreparedStatementPerConnectionSize());

        dataSource.incrementDupCloseCount();
        assertEquals(1, dataSource.getDupCloseCount());

        dataSource.setValidConnectionChecker(null);
        dataSource.setValidConnectionCheckerClassName(null);
        assertEquals(null, dataSource.getValidConnectionChecker());

        dataSource.addConnectionProperty("user", "ljw");
        assertEquals(1, dataSource.getConnectProperties().size());

        assertEquals(0, dataSource.getConnectionInitSqls().size());
        dataSource.setConnectionInitSqls(Arrays.<Object> asList("SELECT 1", null, ""));
        assertEquals(1, dataSource.getConnectionInitSqls().size());

        assertEquals(500, dataSource.getTimeBetweenConnectErrorMillis());
        assertEquals(234, dataSource.getMaxOpenPreparedStatements());
        assertEquals(300, dataSource.getRemoveAbandonedTimeout());
        dataSource.setRemoveAbandonedTimeout(400);
        assertEquals(400, dataSource.getRemoveAbandonedTimeout());
        assertEquals(400 * 1000, dataSource.getRemoveAbandonedTimeoutMillis());
        assertEquals(3, dataSource.getNumTestsPerEvictionRun());
        dataSource.setNumTestsPerEvictionRun(4);
        assertEquals(4, dataSource.getNumTestsPerEvictionRun());

        dataSource.setMaxWaitThreadCount(4);
        assertEquals(4, dataSource.getMaxWaitThreadCount());

        dataSource.setValidationQueryTimeout(4);
        assertEquals(4, dataSource.getValidationQueryTimeout());

        dataSource.setAccessToUnderlyingConnectionAllowed(true);
        assertEquals(true, dataSource.isAccessToUnderlyingConnectionAllowed());

        dataSource.setDefaultReadOnly(true);
        assertEquals(Boolean.TRUE, dataSource.getDefaultReadOnly());

        dataSource.setDefaultTransactionIsolation(10);
        assertEquals(Integer.valueOf(10), dataSource.getDefaultTransactionIsolation());

        dataSource.setDefaultCatalog("xxx");
        assertEquals("xxx", dataSource.getDefaultCatalog());

        dataSource.setPasswordCallbackClassName(null);
        dataSource.setUserCallback(null);

        assertEquals(0, dataSource.getQueryTimeout());
        dataSource.setQueryTimeout(10001);
        assertEquals(10001, dataSource.getQueryTimeout());

        assertEquals(-1, dataSource.getMaxWait());
        dataSource.setMaxWait(10001);
        assertEquals(10001, dataSource.getMaxWait());

        assertEquals(8, dataSource.getMaxIdle());
        dataSource.setMaxIdle(3);
        assertEquals(3, dataSource.getMaxIdle());

        assertEquals(0, dataSource.getLoginTimeout());
        dataSource.setLoginTimeout(30);
        assertEquals(30, dataSource.getLoginTimeout());

        assertEquals(null, dataSource.getUsername());
        dataSource.setUsername("ljw");
        assertEquals("ljw", dataSource.getUsername());

        assertEquals(null, dataSource.getPassword());
        dataSource.setPassword("xxx");
        assertEquals("xxx", dataSource.getPassword());

        dataSource.setConnectProperties(new Properties());
        assertEquals(0, dataSource.getConnectProperties().size());
        dataSource.setConnectionProperties("a=1;b=2;c");
        assertEquals(3, dataSource.getConnectProperties().size());

        dataSource.setExceptionSorter((ExceptionSorter) null);

        dataSource.close();
    }

    protected void tearDown() throws Exception {
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }

        super.tearDown();
    }

}
