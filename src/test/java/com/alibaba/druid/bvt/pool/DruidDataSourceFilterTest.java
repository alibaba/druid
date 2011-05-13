package com.alibaba.druid.bvt.pool;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceFilterTest extends TestCase {
    public void test_filter() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        
        Assert.assertEquals(0, dataSource.getFilters().size());
        
        dataSource.setFilters("stat");
        
        Assert.assertEquals(1, dataSource.getFilters().size());
    }
    
    public void test_filter_2() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        
        Assert.assertEquals(0, dataSource.getFilters().size());
        
        dataSource.setFilters("stat,trace");
        
        Assert.assertEquals(2, dataSource.getFilters().size());
    }
}
