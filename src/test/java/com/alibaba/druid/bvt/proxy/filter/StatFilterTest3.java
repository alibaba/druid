package com.alibaba.druid.bvt.proxy.filter;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.stat.StatFilter;

public class StatFilterTest3 extends TestCase {
    @SuppressWarnings("deprecation")
    public void test_dbType() throws Exception {
        StatFilter filter = new StatFilter();
        
        Assert.assertFalse(filter.isMergeSql());
        
        filter.setDbType("mysql");
        filter.setMergeSql(true);
        
        Assert.assertTrue(filter.isMergeSql());
        Assert.assertEquals("mysql", filter.getDbType());
        
        Assert.assertEquals("SELECT ?\nLIMIT ?" , filter.mergeSql("select 'x' limit 1"));
    }
    
    public void test_dbType_error() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType("mysql");
        filter.setMergeSql(true);
        
        Assert.assertEquals("mysql", filter.getDbType());
        
        Assert.assertEquals("sdafawer asf " , filter.mergeSql("sdafawer asf "));
    }
    
    public void test_merge() throws Exception {
        StatFilter filter = new StatFilter();
        filter.setDbType("mysql");
        filter.setMergeSql(false);
        
        Assert.assertEquals("mysql", filter.getDbType());
        
        Assert.assertEquals("select 'x' limit 1" , filter.mergeSql("select 'x' limit 1"));
    }
    
}
