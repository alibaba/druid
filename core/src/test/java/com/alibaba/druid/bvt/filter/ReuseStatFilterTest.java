package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReuseStatFilterTest {
    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSourceA = new DruidDataSource();
        dataSourceB = new DruidDataSource();

        dataSourceA.setUrl("jdbc:mock:xxx_A");
        dataSourceB.setUrl("jdbc:mock:xxx_B");

        StatFilter filter = new StatFilter();

        dataSourceA.getProxyFilters().add(filter);
        dataSourceB.getProxyFilters().add(filter);

        dataSourceA.init();
        dataSourceB.init();
    }

    @Test
    public void test_execute() throws Exception {
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSourceA);
        JdbcUtils.close(dataSourceB);
    }
}
