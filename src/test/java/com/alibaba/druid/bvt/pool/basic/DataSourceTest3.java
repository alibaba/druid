package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.security.auth.callback.PasswordCallback;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class DataSourceTest3 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat,trace");

        JdbcStatContext context = new JdbcStatContext();
        context.setTraceEnable(true);
        JdbcStatManager.getInstance().setStatContext(context);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        JdbcStatManager.getInstance().setStatContext(null);
    }

    public void test_setUser() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setUsername(dataSource.getUsername());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setUsername("xxx_u1");
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
        }
    }

    public void test_error_1() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        dataSource.setPoolPreparedStatements(false);
    }

    public void test_error_2() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setMaxWait(1);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_3() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setMinIdle(1);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_4() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setMaxIdle(1);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_5() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setInitialSize(1);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_6() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setMaxActive(1);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_setPassword() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setPassword(dataSource.getPassword());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setPassword("xxx_pass");
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
        }
    }
    
    public void test_setUrlAndUserAndPassword() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setUrlAndUserAndPassword(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setUrlAndUserAndPassword(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword() + "_");
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
        }
    }
    
    public void test_setUrlAndUserAndPassword_1() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setUrlAndUserAndPassword(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setUrlAndUserAndPassword(dataSource.getUrl() + "_", dataSource.getUsername(), dataSource.getPassword() );
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
        }
    }
    
    public void test_setUrlAndUserAndPassword_2() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setUrlAndUserAndPassword(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setUrlAndUserAndPassword(dataSource.getUrl(), dataSource.getUsername() + "_", dataSource.getPassword() );
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
        }
    }

    public void test_error_8() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setConnectProperties(new Properties());
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public void test_error_9() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);

        conn.close();

        {
            Exception error = null;
            try {
                dataSource.setConnectionProperties("x=12;;");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
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

    public void test_setUrl() throws Exception {
        Connection real;

        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            real = conn.getConnection();
            conn.close();
        }
        
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }

        dataSource.setUrl(dataSource.getUrl());
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertSame(real, conn.getConnection());
            conn.close();
        }
        dataSource.setUrl("jdbc:mock:xxx_123");
        {
            DruidPooledConnection conn = dataSource.getConnection().unwrap(DruidPooledConnection.class);
            Assert.assertNotSame(real, conn.getConnection());
            conn.close();
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
