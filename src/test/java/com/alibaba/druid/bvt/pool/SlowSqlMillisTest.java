package com.alibaba.druid.bvt.pool;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;


public class SlowSqlMillisTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:wrap-jdbc:jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(false);
        dataSource.setConnectionProperties("druid.stat.slowSqlMillis=500");
        dataSource.setFilters("stat");
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_connect() throws Exception {
        StatFilter filter = (StatFilter) dataSource.getProxyFilters().get(0);
        Assert.assertEquals(500, filter.getSlowSqlMillis());
    }
}
