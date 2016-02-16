package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试initialSize > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getPoolingPeakTime extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getPoolingPeakTime());
        Assert.assertNull(dataSource.getActivePeakTime());
        
        Connection conn = dataSource.getConnection();
        conn.close();
        
        Assert.assertNotNull(dataSource.getPoolingPeakTime());
        Assert.assertNotNull(dataSource.getActivePeakTime());
    }
}
