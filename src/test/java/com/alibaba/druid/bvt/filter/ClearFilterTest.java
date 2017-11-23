package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.encoding.EncodingConvertFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;


public class ClearFilterTest extends PoolTestCase {
    public void test_filters() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        Assert.assertEquals(0, dataSource.getProxyFilters().size());
        dataSource.setFilters("encoding");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.setFilters("!stat");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        Assert.assertEquals(StatFilter.class.getName(), dataSource.getFilterClassNames().get(0));
        dataSource.setClearFiltersEnable(false);
        dataSource.setFilters("!encoding");
        Assert.assertEquals(StatFilter.class.getName(), dataSource.getFilterClassNames().get(0));
        Assert.assertEquals(EncodingConvertFilter.class.getName(), dataSource.getFilterClassNames().get(1));
        
        dataSource.setConnectionProperties("druid.clearFiltersEnable=false");
        Assert.assertFalse(dataSource.isClearFiltersEnable());
        
        dataSource.setConnectionProperties("druid.clearFiltersEnable=true");
        Assert.assertTrue(dataSource.isClearFiltersEnable());
        
        dataSource.setConnectionProperties("druid.clearFiltersEnable=xx"); // no change
        Assert.assertTrue(dataSource.isClearFiltersEnable());
        
        dataSource.close();
    }
}
