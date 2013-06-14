package com.alibaba.druid.bvt.filter;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;


public class ClearFilterTest extends TestCase {
    public void test_filters() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        Assert.assertEquals(0, dataSource.getProxyFilters().size());
        dataSource.setFilters("encoding");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.setFilters("!stat");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        Assert.assertEquals(StatFilter.class.getName(), dataSource.getFilterClassNames().get(0));
        dataSource.close();
    }
}
