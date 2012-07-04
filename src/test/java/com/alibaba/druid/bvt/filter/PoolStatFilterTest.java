package com.alibaba.druid.bvt.filter;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.JdbcUtils;

public class PoolStatFilterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {
        
        Assert.assertTrue(dataSource.isInited());
        
        Histogram histogram = dataSource.getDataSourceStat().getConnectionHoldHistogram();
        
        Assert.assertEquals(0, histogram.getValue(0));

        Connection conn = dataSource.getConnection();

        conn.close();

        Assert.assertEquals(1, histogram.getValue(0));
    }
}
