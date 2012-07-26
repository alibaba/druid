package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestPoolPreparedStatement2 extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.cear();

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
        dataSource.setPoolPreparedStatements(false);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        
//        ((StatFilter) dataSource.getProxyFilters().get(0)).setMaxSqlStatCount(100);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_stmtCache() throws Exception {
        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }

        dataSource.setPoolPreparedStatements(true);

        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 10; ++i) {
                Connection conn = dataSource.getConnection();
                String sql = "SELECT" + i;
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.execute();
                stmt.close();
                conn.close();
            }
        }

        for (int i = 0; i < 1000 * 1; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT " + i);
            stmt.execute();
            stmt.close();
            conn.close();
        }

        Connection conn = dataSource.getConnection();
        DruidPooledConnection poolableConn = conn.unwrap(DruidPooledConnection.class);
        Assert.assertNotNull(poolableConn);

        Assert.assertEquals(dataSource.getMaxPoolPreparedStatementPerConnectionSize(),
                            poolableConn.getConnectionHolder().getStatementPool().getMap().size());

        conn.close();

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}
