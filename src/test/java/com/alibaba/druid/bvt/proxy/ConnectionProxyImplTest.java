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
package com.alibaba.druid.bvt.proxy;

import java.sql.SQLClientInfoException;
import java.util.Properties;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionProxyImplTest extends TestCase {

    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_connection() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        DataSourceProxy dataSource = new DataSourceProxyImpl(null, config);

        FilterEventAdapter filter = new FilterEventAdapter() {
        };
        filter.init(dataSource);

        ConnectionProxyImpl rawConnection = new ConnectionProxyImpl(null, null, new Properties(), 0) {

            public void setClientInfo(String name, String value) throws SQLClientInfoException {

            }
        };

        ConnectionProxyImpl connection = new ConnectionProxyImpl(dataSource, rawConnection, new Properties(), 1001);

        connection.setClientInfo("name", null);
    }

}
