package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDataSourceBasic extends TestCase {

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
        dataSource.setRemoveAbandoned(true);
        dataSource.setExceptionSorterClassName(null);
        
        Assert.assertTrue(dataSource.getExceptionSoter() instanceof NullExceptionSorter);
        dataSource.setExceptionSorterClassName("");
        Assert.assertTrue(dataSource.getExceptionSoter() instanceof NullExceptionSorter);
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(true, dataSource.getCreateTimespanNano() > 0);
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }
    
    public void test_toCompositeData() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
        dataSource.getCompositeData();
    }

    public void test_prepare() throws Exception {
        Connection conn = dataSource.getConnection();

        {
            PoolableConnection wrap = conn.unwrap(PoolableConnection.class);
            Assert.assertTrue(conn.isWrapperFor(PoolableConnection.class));
            Assert.assertNotNull(wrap);
        }
        
        {
            Statement wrap = conn.unwrap(Statement.class);
            Assert.assertTrue(!conn.isWrapperFor(Statement.class));
            Assert.assertNull(wrap);
        }

        conn.setAutoCommit(false);
        conn.setAutoCommit(false);
        Assert.assertEquals(1, dataSource.getActiveConnectionStackTrace().size());
        Assert.assertEquals(1, dataSource.getActiveConnections().size());
        conn.commit();
        conn.close();

        Assert.assertEquals(1, dataSource.getStartTransactionCount());
        Assert.assertEquals(1, dataSource.getCommitCount());
        Assert.assertEquals(0, dataSource.getRollbackCount());

        Assert.assertEquals(0, dataSource.getActiveConnectionStackTrace().size());
        Assert.assertEquals(0, dataSource.getActiveConnections().size());
    }
}
