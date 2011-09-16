package com.alibaba.druid.bvt.pool.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class ConnectionTest5 extends TestCase {

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
        dataSource.setTestOnBorrow(false);
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

    public void test_basic() throws Exception {
        PoolableConnection conn = (PoolableConnection) dataSource.getConnection();
        conn.close();
        
        Assert.assertEquals(true, dataSource.isResetStatEnable());
        dataSource.setResetStatEnable(false);
        Assert.assertEquals(false, dataSource.isResetStatEnable());
        Assert.assertEquals(1, dataSource.getConnectCount());
        dataSource.resetStat();
        Assert.assertEquals(1, dataSource.getConnectCount());
        
        dataSource.setResetStatEnable(true);
        dataSource.resetStat();
        Assert.assertEquals(0, dataSource.getConnectCount());
        
    }
    
 
}
