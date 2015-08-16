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
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.DruidPasswordCallback;

public class PasswordCallbackTest extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        TestPasswordCallback passwordCallback = new TestPasswordCallback();
        dataSource.setPasswordCallback(passwordCallback);

        Connection conn = dataSource.getConnection();
        conn.close();

        Assert.assertEquals(dataSource.getUrl(), passwordCallback.getUrl());
        Assert.assertEquals(dataSource.getConnectProperties(), passwordCallback.getProperties());

        dataSource.close();
    }

    public static class TestPasswordCallback extends DruidPasswordCallback {

        private static final long serialVersionUID = 1L;

        private Properties        properties;

        public TestPasswordCallback(){
            super("test", false);
        }

        public TestPasswordCallback(String prompt, boolean echoOn){
            super(prompt, echoOn);
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

    }
}
