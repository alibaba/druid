package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestPoolPreparedStatement2 extends TestCase {

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
        dataSource.setTimeBetweenEvictionRunsMillis(10); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_stmtCache() throws Exception {

        for (int i = 0; i < 1000 * 1000 * 10; ++i){
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT " + i);
            stmt.close();
            conn.close();
        }

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}
