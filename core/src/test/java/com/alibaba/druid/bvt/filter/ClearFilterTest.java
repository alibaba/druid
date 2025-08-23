package com.alibaba.druid.bvt.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


import com.alibaba.druid.filter.encoding.EncodingConvertFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;


public class ClearFilterTest extends PoolTestCase {
    public void test_filters() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        assertEquals(0, dataSource.getProxyFilters().size());
        dataSource.setFilters("encoding");
        assertEquals(1, dataSource.getProxyFilters().size());
        dataSource.setFilters("!stat");
        assertEquals(1, dataSource.getProxyFilters().size());
        assertEquals(StatFilter.class.getName(), dataSource.getFilterClassNames().get(0));
        dataSource.setClearFiltersEnable(false);
        dataSource.setFilters("!encoding");
        assertEquals(StatFilter.class.getName(), dataSource.getFilterClassNames().get(0));
        assertEquals(EncodingConvertFilter.class.getName(), dataSource.getFilterClassNames().get(1));

        dataSource.setConnectionProperties("druid.clearFiltersEnable=false");
        assertFalse(dataSource.isClearFiltersEnable());

        dataSource.setConnectionProperties("druid.clearFiltersEnable=true");
        assertTrue(dataSource.isClearFiltersEnable());

        dataSource.setConnectionProperties("druid.clearFiltersEnable=xx"); // no change
        assertTrue(dataSource.isClearFiltersEnable());

        dataSource.close();
    }
}
