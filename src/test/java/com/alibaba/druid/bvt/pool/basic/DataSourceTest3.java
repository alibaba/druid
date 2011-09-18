package com.alibaba.druid.bvt.pool.basic;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;
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

    public void test_prepareStatement_error() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);

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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        {
            Exception error = null;
            try {
                dataSource.setPoolPreparedStatements(false);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_error_2() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
    
    public void test_error_7() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        {
            Exception error = null;
            try {
                dataSource.setPassword("xx");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_error_8() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        {
            Exception error = null;
            try {
                dataSource.setConnectionProperties("x=12");
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
    
    public void test_getValidConnectionCheckerClassName() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        dataSource.getValidConnectionCheckerClassName();
    }
    
    public void test_setConnectionInitSqls() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        dataSource.setConnectionInitSqls(null);
    }
    
    public void test_setConnectionProperties() throws Exception {
        dataSource.setConnectionProperties(null);
        dataSource.setLogWriter(null);
        dataSource.getLogWriter();
        
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
        conn.close();
        
        
    }

    
    public void test_error_10() throws Exception {
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
        PoolableConnection conn = dataSource.getConnection().unwrap(PoolableConnection.class);
        
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
}
