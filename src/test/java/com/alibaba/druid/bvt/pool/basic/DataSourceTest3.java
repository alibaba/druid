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

import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.security.auth.callback.PasswordCallback;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class DataSourceTest3 extends PoolTestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

        JdbcStatContext context = new JdbcStatContext();
        context.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(context);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        JdbcStatManager.getInstance().setStatContext(null);

        super.tearDown();
    }

    public void test_prepareStatement_error() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setUsername("xxx");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_1() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setPoolPreparedStatements(false);
    }

    public void test_change_maxWait() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        conn.close();
        dataSource.setMaxWait(1);
    }

    public void test_change_minIdle() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setMinIdle(1);
    }

    @SuppressWarnings("deprecation")
    public void test_error_4() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setMaxIdle(1);
    }

    public void test_error_5() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setInitialSize(dataSource.getInitialSize());

        {
            Exception error = null;
            try {
                dataSource.setInitialSize(10);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_6() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setMaxActive(1);
    }

    public void test_error_7() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setPassword(dataSource.getPassword());
        dataSource.setPassword("xx");
    }

    public void test_change_connectProperties() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setConnectProperties(new Properties());
    }

    public void test_change_connectProperties_2() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setConnectionProperties("x=12;;");
    }

    public void test_getValidConnectionCheckerClassName() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.getValidConnectionCheckerClassName();
    }

    public void test_setConnectionInitSqls() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setConnectionInitSqls(null);
    }

    public void test_setConnectionProperties() throws Exception {
        dataSource.setConnectionProperties(null);
        dataSource.setLogWriter(null);
        dataSource.getLogWriter();

        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

    }

    public void test_error_10() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.addConnectionProperty("x", "11");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_11() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.getUrl();

        {
            Exception error = null;
            try {
                dataSource.setUrl("x");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setConnectionInitSqls_2() throws Exception {
        dataSource.setConnectionInitSqls(Collections.emptyList());
        dataSource.setConnectionInitSqls(Collections.singleton(null));
    }

    public void test_ValidConnectionChecker() throws Exception {
        dataSource.getValidConnectionCheckerClassName();
        dataSource.setValidConnectionChecker(new MySqlValidConnectionChecker());
        Assert.assertEquals(MySqlValidConnectionChecker.class.getName(),
                            dataSource.getValidConnectionCheckerClassName());
    }

    public void test_setConnectionInitSqls_1() throws Exception {
        dataSource.setConnectionInitSqls(Collections.emptyList());
        dataSource.setConnectionInitSqls(Collections.singleton(null));
    }

    public static class MyPasswordCallbackClassName extends PasswordCallback {

        public MyPasswordCallbackClassName(){
            super("password", false);
        }

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }

    public void test_setPasswordCallbackClassName() throws Exception {
        dataSource.setPasswordCallbackClassName(MyPasswordCallbackClassName.class.getName());
    }

    public void test_error_12() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setDriverClassName("");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setExceptionSorter() throws Exception {
        dataSource.setExceptionSorter(NullExceptionSorter.class.getName());
    }

    public void test_setProxyFilters() throws Exception {
        dataSource.setProxyFilters(null);
        dataSource.setFilters(null);
        dataSource.setFilters("");
    }

    public void test_error_validateConnection() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.validateConnection(conn);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_validateConnection_2() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.getConnection().close();

        {
            Exception error = null;
            try {
                dataSource.validateConnection(conn);
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_validateConnection_3() throws Exception {
        dataSource.setValidationQuery(null);
        dataSource.setValidConnectionChecker(new MySqlValidConnectionChecker());

        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
        dataSource.validateConnection(conn);

    }
}
