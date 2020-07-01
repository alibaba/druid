package com.alibaba.druid.bvt.pool;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
        dataSource.setDbType("oracle");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriver(new MockDriver() {

            @Override
            public int getMajorVersion() {
                return 10;
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_oracle() throws Exception {
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.init();
        Assert.assertTrue(dataSource.isOracle());
        Assert.assertEquals("true", dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        
        dataSource.setUseOracleImplicitCache(false);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));

        dataSource.setUseOracleImplicitCache(true);
        dataSource.setUseOracleImplicitCache(true);
        Assert.assertEquals("true", dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        
        dataSource.setUseOracleImplicitCache(false);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.setDriver(null);
        
        dataSource.setUseOracleImplicitCache(true);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
    }
}
