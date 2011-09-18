package com.alibaba.druid.bvt.pool.adapter;

import javax.sql.DataSource;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class DruidDataSourceC3P0AdapterTest2 extends TestCase {

    public void test_0() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
        dataSource.setJdbcUrl("jdbc:mock:xxx");
        
        dataSource.setLogWriter(dataSource.getLogWriter());
        dataSource.setLoginTimeout(dataSource.getLoginTimeout());
        
        Assert.assertTrue(dataSource.isWrapperFor(DruidDataSourceC3P0Adapter.class));
        Assert.assertTrue(dataSource.isWrapperFor(DruidDataSource.class));
        Assert.assertTrue(dataSource.isWrapperFor(DataSource.class));
        Assert.assertFalse(dataSource.isWrapperFor(null));
        
        Assert.assertNotNull(dataSource.unwrap(DruidDataSourceC3P0Adapter.class));
        Assert.assertNotNull(dataSource.unwrap(DruidDataSource.class));
        Assert.assertNotNull(dataSource.unwrap(DataSource.class));
        Assert.assertNull(dataSource.unwrap(null));
        
        dataSource.setProperties(dataSource.getProperties());
        dataSource.setUser(dataSource.getUser());

        dataSource.close();
    }

    protected void tearDown() throws Exception {
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }
    }
}
