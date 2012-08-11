package com.alibaba.druid.bvt.pool;

import java.util.Hashtable;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDataSourceFactoryTest extends TestCase {

    private DruidDataSource dataSource;
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_factory() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();

        Reference ref = new Reference(DataSource.class.getName());
        ref.add(new StringRefAddr(DruidDataSourceFactory.PROP_REMOVEABANDONED, "true"));
        ref.add(new StringRefAddr(DruidDataSourceFactory.PROP_MAXACTIVE, "20"));

        Hashtable<String, String> env = new Hashtable<String, String>();

        dataSource = (DruidDataSource) factory.getObjectInstance(ref, null, null, env);

        Assert.assertTrue(dataSource.isRemoveAbandoned());
        Assert.assertEquals(20, dataSource.getMaxActive());
    }
}
