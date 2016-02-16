package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DataSourceDisableException;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_enable extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxWait(1000);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_disable() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        Assert.assertTrue(dataSource.isEnable());

        dataSource.setEnable(false);

        Assert.assertFalse(dataSource.isEnable());

        dataSource.shrink();

        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_disable_() throws Exception {
        dataSource.setEnable(false);
        
        Assert.assertFalse(dataSource.isEnable());
        
        Exception error = null;
        try {
            Connection conn = dataSource.getConnection();
            conn.close();
        } catch (DataSourceDisableException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
