package com.alibaba.druid.bvt.pool.basic;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.RefAddr;
import javax.naming.Reference;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSourceFactory;


public class DruidDataSourceFactoryTest extends TestCase {
    @SuppressWarnings("serial")
    public void test_factory() throws Exception {
        DruidDataSourceFactory factory = new DruidDataSourceFactory();
        
        Assert.assertNull(factory.getObjectInstance(null, null, null, null));
        Assert.assertNull(factory.getObjectInstance(new Reference("javax.sql.Date"), null, null, null));
        
        Reference ref = new Reference("javax.sql.DataSource");
        ref.add(new RefAddr("user") {
            @Override
            public Object getContent() {
                return null;
            }
            
        });
        ref.add(new RefAddr("defaultReadOnly") {
            @Override
            public Object getContent() {
                return Boolean.TRUE;
            }
            
        });
        
        factory.getObjectInstance(ref, null, null, new Hashtable<Object, Object>());
    }
    
    public void test_createDataSource() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("defaultAutoCommit", "true");
        properties.setProperty("defaultReadOnly", "true");
        properties.setProperty("defaultTransactionIsolation", "NONE");
        properties.setProperty("defaultCatalog", "cn");
        properties.setProperty("driverClassName", "com.alibaba.druid.mock.MockDriver");
        properties.setProperty("maxActive", "8");
        properties.setProperty("maxIdle", "8");
        properties.setProperty("minIdle", "3");
        properties.setProperty("initialSize", "1");
        properties.setProperty("maxWait", "-1");
        properties.setProperty("testOnBorrow", "true");
        properties.setProperty("testOnReturn", "true");
        properties.setProperty("timeBetweenEvictionRunsMillis", "3000");
        properties.setProperty("numTestsPerEvictionRun", "1");
        properties.setProperty("minEvictableIdleTimeMillis", "10000");
        properties.setProperty("testWhileIdle", "true");
        properties.setProperty("password", "xxx");
        properties.setProperty("url", "jdbc:mock:xxx");
        properties.setProperty("username", "user");
        properties.setProperty("validationQuery", "select 1");
        properties.setProperty("validationQueryTimeout", "30");
        
        DruidDataSourceFactory.createDataSource(properties);
    }
}
