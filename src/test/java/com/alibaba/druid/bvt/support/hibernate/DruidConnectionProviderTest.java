package com.alibaba.druid.bvt.support.hibernate;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.support.hibernate.DruidConnectionProvider;


public class DruidConnectionProviderTest extends TestCase {
    private DruidConnectionProvider provider;
    
    protected void setUp() throws Exception {
        provider = new DruidConnectionProvider();
        
        Map properties = new HashMap<String, Object>();
        properties.put("url", "jdbc:mock:xxx");
        
        provider.configure(properties);
    }
    
    protected void tearDown() throws Exception {
        provider.stop();
    }
    
    public void test_hibernate() throws Exception {
        Connection conn = provider.getConnection();
        Assert.assertFalse(conn.isClosed());
        
        provider.closeConnection(conn);
        Assert.assertTrue(conn.isClosed());
    }
}
